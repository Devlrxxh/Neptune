# Placeholders

## Globally Available

| Plugin | PlaceholderAPI | Description |
| ------ | -------------- | ----------- |
| \<online> | None<sup style="color: red">*</sup> | Use server expansion for PAPI |
| \<queued> | %neptune_queued% | Get the number of players in queue |
| \<in-match> | %neptune_in-match% | Get the number of players in matches |
| \<player> | None<sup style="color: red">*</sup> | The name of the player |
| \<ping> | %neptune_ping% | The ping of the player in milliseconds |
| \<wins> | %neptune_wins% | The number of wins a player has accumulated |
| \<losses> | %neptune_losses%` | The number of losses a player has accumulated |
| \<currentStreak> | %neptune_currentStreak% | The current win streak of the player |

## In Queue

| Plugin | PlaceholderAPI | Description |
| ------ | -------------- | ----------- |
| \<kit> | %neptune_kit% | The display name of the kit the player is playing on |
| \<maxPing> | %neptune_maxPing% | The maximum ping allowed by the player in their settings |
| \<time> | %neptune_time% | The time in minutes and seconds that the player has been queueing for |

## Kit Editor

| Plugin | PlaceholderAPI | Description |
| ------ | -------------- | ----------- |
| \<kit> | %neptune_kit% | The display name of the kit the player is editing |

## Party

| Plugin | PlaceholderAPI | Description |
| ------ | -------------- | ----------- |
| \<leader> | %neptune_leader% | The name of the leader of the party |
| \<size> | %neptune_size% | The number of members in the party |
| \<party-max> | %neptune_party-max% | The maximum number of players that can be in the party |

## Any Match

| Plugin | PlaceholderAPI | Description |
| ------ | -------------- | ----------- |
| \<red-bed-status><sup style="color: red">**</sup> | %neptune_red-bed-broken%<sup style="color: red">$</sup> | The ping of the player in the red team |
| \<blue-bed-status><sup style="color: red">**</sup> | %neptune_blue-bed-broken%<sup style="color: red">$</sup> | The ping of the player in the blue team |
| \<time> | %neptune_time% | The time the match has been active for |
| \<maxPoints> | %neptune_max-points% | The total number of rounds in a match |
| \<points> | %neptune_points% | The number of rounds won by the player's team |
| \<opponent-points> | %neptune_opponent-points% | The number of rounds won by the opponent's team |
| \<kit> | %neptune_kit% | The display name of the kit the match is being played with |
| \<arena> | %neptune_arena% | The display name of the arena of the match |

## Solo Match

| Plugin | PlaceholderAPI | Description |
| ------ | -------------- | ----------- |
| \<opponent> | %neptune_opponent% | The name of the opponent |
| \<opponent-ping> | %neptune_opponent-ping% | The ping of the opponent in milliseconds |
| \<combo> | %neptune_combo% | The combo the player is holding against the opponent |
| \<opponent-combo> | %neptune_opponent-combo% | The combo the opponent is holding against the player |
| \<hits> | %neptune_hits% | The amount of times the player has hit the opponent |
| \<opponent-hits> | %neptune_opponent-hits% | The amount of times the opponent has hit the player |
| \<diffrence> | %neptune_difference% | The difference in amount of hits between the player and the opponent |
| \<playerRed_name> | %neptune_player-red-name% | The name of the player in the red team |
| \<playerBlue_name> | %neptune_player-blue-name% | The name of the player in the blue team |
| \<playerRed_ping> | %neptune_player-red-ping% | The ping of the player in the red team |
| \<playerBlue_ping> | %neptune_player-blue-ping% | The ping of the player in the blue team |
| \<bed-status><sup style="color: red">**</sup> | %neptune_bed-broken%<sup style="color: red">$</sup> | Whether the player's bed is broken |
| \<opponent-bed-status><sup style="color: red">**</sup> | %neptune_opponent-bed-broken%<sup style="color: red">$</sup> | Whether the opponent's bed is broken |

## Team Match

| Plugin | PlaceholderAPI | Description |
| ------ | -------------- | ----------- |
| \<alive> | %neptune_alive% | The number of players alive on the player's team |
| \<max> | %neptune_max% | The total number of players on the player's team |
| \<alive-opponent> | %neptune_opponent-alive% | The number of players alive on the opposing team |
| \<max-opponent> | %neptune_opponent-max% | The total number of players on the opposing team |
| \<team-bed-status><sup style="color: red">**</sup> | %neptune_bed-broken%<sup style="color: red">$</sup> | Whether the player team's bed is broken |
| \<opponent-team-bed-status><sup style="color: red">**</sup> | %neptune_opponent-bed-broken%<sup style="color: red">$</sup> | Whether the opponent team's bed is broken |
| \<alive-red> | %neptune_red-alive% | The number of players alive on the red team |
| \<alive-blue> | %neptune_blue-alive% | The number of players alive on the blue team |
| \<max-red> | %neptune_red-max% | The total number of players on the red team |
| \<max-blue> | %neptune_blue-max% | The total number of players on the blue team |

## FFA Match

| Plugin | PlaceholderAPI | Description |
| ------ | -------------- | ----------- |
| \<alive> | %neptune_alive% | The number of players alive in the match |
| \<max> | %neptune_max% | The total number of players that participated in the match |

\* -> The placeholder is not needed since you can use other expansions.

\*\* -> The placeholder is only available in BedWars kits.

\$ -> The PlaceholderAPI version of the placeholder returns "true" if the statement is true, and "false" if otherwise.
