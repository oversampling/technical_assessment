Our company often organize events where players compete in matches. Each match results in one player winning and another player losing. You have a record of all the matches played, where each record indicates the winner and the loser of a match.
Task:
Your task is to analyze the match records and generate a report that includes two lists:

List of Undefeated Players: This list should contain all the players who have not lost a single match throughout the tournament.

List of Players with One Loss: This list should contain all the players who have lost exactly one match during the tournament.
Requirements:
Both lists should be sorted in ascending order by player IDs.

Example 1:
Input: matches = [[1,3],[2,3],[3,6],[5,6],[5,7],[4,5],[4,8],[4,9],[10,4],[10,9]]
                          ^
Output: [[1,2,10],[4,5,7,8]]
Explanation:
Players 1, 2, and 10 have not lost any matches.
Players 4, 5, 7, and 8 each have lost one match.
Players 3, 6, and 9 each have lost two matches.
Thus, answer[0] = [1,2,10] and answer[1] = [4,5,7,8].