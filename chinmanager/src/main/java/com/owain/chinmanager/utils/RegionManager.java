package com.owain.chinmanager.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.owain.chinmanager.models.TileFlag;
import com.owain.chinmanager.models.Transport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.CollisionData;
import net.runelite.api.CollisionDataFlag;
import net.runelite.api.GameState;
import net.runelite.api.Tile;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Singleton
@Slf4j
public class RegionManager
{
	private static final int VERSION = 3;
	public static final MediaType JSON_MEDIATYPE = MediaType.parse("application/json");
	public static final String API_URL = "https://collisionmap.xyz";
	public static final Gson GSON = new GsonBuilder().create();

	@Inject
	private Client client;

	@Inject
	private OkHttpClient okHttpClient;

	@Inject
	private ScheduledExecutorService executorService;

	public void sendRegion()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (client.isInInstancedRegion())
		{
			executorService.schedule(() -> {
				try
				{
					Request request = new Request.Builder()
						.get()
						.url(API_URL + "/regions/instance/" + client.getLocalPlayer().getWorldLocation().getRegionID())
						.build();
					Response response = okHttpClient.newCall(request)
						.execute();
					int code = response.code();
					response.close();

					if (code != 200)
					{
						log.error("Instance store request was unsuccessful: {}", code);
					}
				}
				catch (Exception e)
				{
					log.error("Failed to POST: {}", e.getMessage());
					e.printStackTrace();
				}
			}, 5_000, TimeUnit.MILLISECONDS);

			return;
		}

		CollisionData[] col = client.getCollisionMaps();
		if (col == null)
		{
			return;
		}

		List<TileFlag> tileFlags = new ArrayList<>();
		Map<WorldPoint, List<Transport>> transportLinks = buildTransportLinks();
		int plane = client.getPlane();
		CollisionData data = col[plane];
		if (data == null)
		{
			return;
		}

		int[][] flags = data.getFlags();
		for (int x = 0; x < flags.length; x++)
		{
			for (int y = 0; y < flags.length; y++)
			{
				int tileX = x + client.getBaseX();
				int tileY = y + client.getBaseY();
				int flag = flags[x][y];

				// Stop if we reach any tiles which dont have collision data loaded
				// Usually occurs for tiles which are loaded in the 104x104 scene, but are outside the region
				if (flag == 0xFFFFFF)
				{
					continue;
				}

				int regionId = ((tileX >> 6) << 8) | (tileY >> 6);

				// Set the full block flag in case tiles are null (ex. on upper levels)
				TileFlag tileFlag = new TileFlag(tileX, tileY, plane, CollisionDataFlag.BLOCK_MOVEMENT_FULL, regionId);
				Tile tile = Reachable.getAt(client, tileX, tileY, plane);
				if (tile == null)
				{
					tileFlags.add(tileFlag);
					continue;
				}

				tileFlag.setFlag(flag);
				WorldPoint tileCoords = tile.getWorldLocation();

				// Check if we are blocked by objects
				// We don't need to parse west/south because they're checked by parsing adjacent tiles for north/east
				// We also skip the current tile if an adjacent tile does not have their flags loaded
				WorldPoint northernTile = tileCoords.dy(1);
				if (Reachable.getCollisionFlag(client, northernTile) == 0xFFFFFF)
				{
					continue;
				}

				if (Reachable.isObstacle(client, northernTile)
					&& !Reachable.isWalled(Direction.NORTH, tileFlag.getFlag())
				)
				{
					tileFlag.setFlag(tileFlag.getFlag() + CollisionDataFlag.BLOCK_MOVEMENT_NORTH);
				}

				WorldPoint easternTile = tileCoords.dx(1);
				if (Reachable.getCollisionFlag(client, easternTile) == 0xFFFFFF)
				{
					continue;
				}

				if (Reachable.isObstacle(client, easternTile)
					&& !Reachable.isWalled(Direction.EAST, tileFlag.getFlag())
				)
				{
					tileFlag.setFlag(tileFlag.getFlag() + CollisionDataFlag.BLOCK_MOVEMENT_EAST);
				}

				List<Transport> transports = transportLinks.get(tileCoords);
				if (plane == client.getPlane())
				{
					for (Direction direction : Direction.values())
					{
						switch (direction)
						{
							case NORTH:
								if ((Reachable.hasDoor(client, tile, direction) || Reachable.hasDoor(client, northernTile, Direction.SOUTH))
									&& !isTransport(transports, tileCoords, northernTile))
								{
									tileFlag.setFlag(tileFlag.getFlag() - CollisionDataFlag.BLOCK_MOVEMENT_NORTH);
								}

								break;
							case EAST:
								if ((Reachable.hasDoor(client, tile, direction) || Reachable.hasDoor(client, easternTile, Direction.WEST))
									&& !isTransport(transports, tileCoords, easternTile))
								{
									tileFlag.setFlag(tileFlag.getFlag() - CollisionDataFlag.BLOCK_MOVEMENT_EAST);
								}

								break;
						}
					}
				}

				tileFlags.add(tileFlag);
			}
		}

		executorService.schedule(() -> {
			try
			{
				String json = GSON.toJson(tileFlags);
				RequestBody body = RequestBody.create(JSON_MEDIATYPE, json);
				Request request = new Request.Builder()
					.post(body)
					.url(API_URL + "/regions/" + VERSION)
					.build();
				Response response = okHttpClient.newCall(request)
					.execute();
				int code = response.code();
				response.close();

				if (code != 200)
				{
					log.error("Request was unsuccessful: {}", code);
				}
			}
			catch (Exception e)
			{
				log.error("Failed to POST: {}", e.getMessage());
				e.printStackTrace();
			}
		}, 5, TimeUnit.SECONDS);
	}

	public static Map<WorldPoint, List<Transport>> buildTransportLinks()
	{
		Map<WorldPoint, List<Transport>> out = new HashMap<>();
		for (Transport transport : TransportLoader.buildTransports())
		{
			out.computeIfAbsent(transport.getSource(), x -> new ArrayList<>()).add(transport);
		}

		return out;
	}

	public boolean isTransport(List<Transport> transports, WorldPoint from, WorldPoint to)
	{
		if (transports == null)
		{
			return false;
		}

		return transports.stream().anyMatch(t -> t.getSource().equals(from) && t.getDestination().equals(to));
	}
}