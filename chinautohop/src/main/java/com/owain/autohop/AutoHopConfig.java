/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Dalton <delps1001@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.owain.autohop;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;

@ConfigGroup("chinautohop")
public interface AutoHopConfig extends Config
{
	@ConfigTitleSection(
		keyName = "hopTitle",
		name = "Hop",
		description = "",
		position = 0
	)
	default Title hopTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "disableGrandExchange",
		name = "Disable at Grand Exchange",
		description = "Don't hop if your player is at the grand exchange",
		titleSection = "hopTitle",
		position = 1
	)
	default boolean disableGrandExchange()
	{
		return false;
	}

	@ConfigItem(
		keyName = "disableFeroxEnclave",
		name = "Disable at Ferox Enclave",
		description = "Don't hop if your player is at the Ferox Enclave",
		titleSection = "hopTitle",
		position = 2
	)
	default boolean disableFeroxEnclave()
	{
		return false;
	}

	@ConfigItem(
		keyName = "cmbBracket",
		name = "Within combat bracket",
		description = "Only hop if the player is within your combat bracket",
		titleSection = "hopTitle",
		position = 3
	)
	default boolean cmbBracket()
	{
		return true;
	}

	@ConfigItem(
		keyName = "alwaysHop",
		name = "Hop on player spawn",
		description = "Hop when a player  spawns",
		titleSection = "hopTitle",
		position = 4
	)
	default boolean alwaysHop()
	{
		return true;
	}

	@ConfigItem(
		keyName = "chatHop",
		name = "Hop on chat message",
		description = "Hop whenever any message is entered into chat",
		titleSection = "hopTitle",
		position = 5
	)
	default boolean chatHop()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hopRadius",
		name = "Hop radius",
		description = "Hop only when another player enters radius",
		titleSection = "hopTitle",
		position = 6
	)
	default boolean hopRadius()
	{
		return false;
	}

	@ConfigItem(
		keyName = "playerRadius",
		name = "Player radius",
		description = "Radius (tiles) for player to be within to trigger hop",
		titleSection = "hopTitle",
		position = 7,
		hidden = true,
		unhide = "hopRadius"
	)
	default int playerRadius()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "skulledHop",
		name = "Skulled",
		description = "Hop when a player within your combat bracket spawns that has a skull",
		titleSection = "hopTitle",
		position = 8,
		hide = "alwaysHop"
	)
	default boolean skulledHop()
	{
		return true;
	}

	@ConfigItem(
		keyName = "underHop",
		name = "Log under",
		description = "Hop when a player within your combat bracket spawns underneath you",
		titleSection = "hopTitle",
		position = 9,
		hide = "alwaysHop"
	)
	default boolean underHop()
	{
		return true;
	}

	@ConfigItem(
		keyName = "logout",
		name = "Logout instead",
		description = "Logout instead of hopping, 1 tick faster",
		titleSection = "hopTitle",
		position = 10
	)
	default boolean logout()
	{
		return false;
	}

	@ConfigItem(
		keyName = "returnInventory",
		name = "Return to inventory",
		description = "Return to inventory after hopping",
		titleSection = "hopTitle",
		position = 11,
		hide = "logout"
	)
	default boolean returnInventory()
	{
		return false;
	}

	@ConfigTitleSection(
		keyName = "worldsTitle",
		name = "Worlds",
		description = "",
		position = 12,
		hide = "logout"
	)
	default Title worldsTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "american",
		name = "American",
		description = "Allow hopping to American worlds",
		titleSection = "worldsTitle",
		position = 13,
		hide = "logout"
	)
	default boolean american()
	{
		return true;
	}

	@ConfigItem(
		keyName = "unitedkingdom",
		name = "UK",
		description = "Allow hopping to UK worlds",
		titleSection = "worldsTitle",
		position = 14,
		hide = "logout"
	)
	default boolean unitedkingdom()
	{
		return true;
	}

	@ConfigItem(
		keyName = "germany",
		name = "German",
		description = "Allow hopping to German worlds",
		titleSection = "worldsTitle",
		position = 15,
		hide = "logout"
	)
	default boolean germany()
	{
		return true;
	}

	@ConfigItem(
		keyName = "australia",
		name = "Australian",
		description = "Allow hopping to Australian worlds",
		titleSection = "worldsTitle",
		position = 16,
		hide = "logout"
	)
	default boolean australia()
	{
		return true;
	}

	@ConfigTitleSection(
		keyName = "ignoresTitle",
		name = "Ignore",
		description = "",
		position = 17
	)
	default Title ignoresTitle()
	{
		return new Title();
	}

	@ConfigItem(
		keyName = "friends",
		name = "Friends",
		description = "Don't hop when the player spawned is on your friend list",
		titleSection = "ignoresTitle",
		position = 18
	)
	default boolean friends()
	{
		return true;
	}

	@ConfigItem(
		keyName = "clanmembers",
		name = "Clan members",
		description = "Don't hop when the player spawned is in your clan chat",
		titleSection = "ignoresTitle",
		position = 19
	)
	default boolean clanmember()
	{
		return true;
	}
}