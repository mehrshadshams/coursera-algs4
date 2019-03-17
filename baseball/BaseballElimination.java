/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseballElimination {
    private final Map<String, Integer> team2Index;
    private final Team[] teams;

    private Set<String> subset;
    private String lastCheckedTeam;
    private boolean lastCheckedTeamIsEliminated;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);

        final int numberOfTeams = Integer.parseInt(in.readLine());
        teams = new Team[numberOfTeams];
        team2Index = new HashMap<>();

        int team = 0;
        while (in.hasNextLine()) {
            final String nextLine = in.readLine();
            String[] parts = nextLine.split("\\s+");
            int i = 0;
            for (; i < parts.length; i++) {
                if (!"".equals(parts[i])) {
                    break;
                }
            }
            final String name = parts[i].trim();
            final int wins = Integer.parseInt(parts[i + 1]);
            final int losses = Integer.parseInt(parts[i + 2]);
            final int rem = Integer.parseInt(parts[i + 3]);

            int[] games = new int[numberOfTeams];
            for (int j = 0; j < numberOfTeams; j++) {
                games[j] = Integer.parseInt(parts[i + 4 + j]);
            }

            teams[team] = new Team(name, wins, losses, rem, games);

            team2Index.put(name, team++);
        }
    }

    // number of teams
    public int numberOfTeams() {
        return teams.length;
    }

    // all teams
    public Iterable<String> teams() {
        List<String> names = new ArrayList<>();
        for (Team t : teams) {
            names.add(t.name);
        }
        return names;
    }

    // number of wins for given team
    public int wins(String team) {
        return getTeamByName(team).wins;
    }

    // number of losses for given team
    public int losses(String team) {
        return getTeamByName(team).losses;
    }

    // number of remaining games for given team
    public int remaining(String team) {
        return getTeamByName(team).remainig;
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!team2Index.containsKey(team1) || !team2Index.containsKey(team2))
            throw new IllegalArgumentException();

        Team t1 = getTeamByName(team1);
        return t1.games[team2Index.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        final Team current = getTeamByName(team);

        if (team.equals(lastCheckedTeam)) {
            return lastCheckedTeamIsEliminated;
        }

        lastCheckedTeamIsEliminated = checkEliminated(current);
        lastCheckedTeam = team;

        return lastCheckedTeamIsEliminated;
    }

    private boolean checkEliminated(Team current) {
        final int numberOfTeams = numberOfTeams();
        final int teamIndex = team2Index.get(current.name);

        subset = new HashSet<>();

        for (int i = 0; i < numberOfTeams; i++) {
            if (i != teamIndex) {
                Team t = teams[i];
                if (current.wins + current.remainig < t.wins) {
                    subset.add(t.name);
                    return true;
                }
            }
        }

        int n = numberOfTeams - 1;
        int numberOfGames = (n * (n - 1)) / 2;

        final int numberOfVertices = 2 + numberOfGames + numberOfTeams - 1;
        final int s = numberOfVertices - 2;
        final int t = numberOfVertices - 1;
        final int initialTeamPos = s - (numberOfTeams - 1);
        int teamPos = initialTeamPos;

        FlowNetwork net = new FlowNetwork(numberOfVertices);

        int[] teamVertexPos = new int[numberOfTeams];
        teamVertexPos[teamIndex] = -1;
        for (int i = 0; i < numberOfTeams; i++) {
            if (i != teamIndex) {
                teamVertexPos[i] = teamPos++;
            }
        }

        int gameVertexPos = 0;
        int sum = 0;
        List<Integer> networkTeams = new ArrayList<>();

        for (int i = 0; i < numberOfTeams; i++) {
            Team t1 = teams[i];

            if (i != teamIndex) {
                for (int j = i + 1; j < numberOfTeams; j++) {
                    if (j != teamIndex) {
                        int gameCount = t1.games[j];
                        sum += gameCount;
                        net.addEdge(new FlowEdge(s, gameVertexPos, gameCount));
                        net.addEdge(
                                new FlowEdge(gameVertexPos, teamVertexPos[i],
                                             Double.POSITIVE_INFINITY));
                        net.addEdge(
                                new FlowEdge(gameVertexPos, teamVertexPos[j],
                                             Double.POSITIVE_INFINITY));

                        gameVertexPos++;
                    }
                }

                net.addEdge(new FlowEdge(teamVertexPos[i], t,
                                         Math.max(0, current.wins + current.remainig - t1.wins)));
                networkTeams.add(i);
            }
        }

        FordFulkerson ff = new FordFulkerson(net, s, t);

        if (ff.value() == sum) {
            return false;
        }

        for (int v = initialTeamPos, i = 0; v < s; v++, i++) {
            if (ff.inCut(v)) {
                subset.add(teams[networkTeams.get(i)].name);
            }
        }

        return true;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!team2Index.containsKey(team)) throw new IllegalArgumentException();
        if (!isEliminated(team)) return null;

        return subset;
    }

    private Team getTeamByName(String team) {
        if (!team2Index.containsKey(team)) throw new IllegalArgumentException();
        return teams[team2Index.get(team)];
    }

    private class Team {
        private String name;
        private int wins;
        private int losses;
        private int remainig;
        private int[] games;

        public Team(String name, int wins, int losses, int remainig, int[] games) {
            this.name = name;
            this.wins = wins;
            this.losses = losses;
            this.remainig = remainig;
            this.games = games;
        }
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
