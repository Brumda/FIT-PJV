package thedrake.code;

import java.io.PrintWriter;
import java.util.*;

public class BoardTroops implements JSONSerializable {
    private final PlayingSide playingSide;
    private final Map<BoardPos, TroopTile> troopMap;
    private final TilePos leaderPosition;
    private final int guards;

    public BoardTroops(PlayingSide playingSide) {
        // Place for your code
        this.playingSide = playingSide;
        this.troopMap = Collections.EMPTY_MAP;
        leaderPosition = TilePos.OFF_BOARD;
        this.guards = 0;
    }


    public BoardTroops(PlayingSide playingSide, Map<BoardPos, TroopTile> troopMap, TilePos leaderPosition, int guards) {
        this.playingSide = playingSide;
        this.troopMap = troopMap;
        this.leaderPosition = leaderPosition;
        this.guards = guards;
    }

    public Optional<TroopTile> at(TilePos pos) {
        return Optional.ofNullable(troopMap.get(pos));
    }

    public PlayingSide playingSide() {
        return playingSide;
    }

    public TilePos leaderPosition() {
        return leaderPosition;
    }

    public int guards() {
        return guards;
    }

    public boolean isLeaderPlaced() {
        return leaderPosition != TilePos.OFF_BOARD;
    }

    public boolean isPlacingGuards() {
        return isLeaderPlaced() && guards < 2;
    }

    public Set<BoardPos> troopPositions() {
        return troopMap.keySet();
    }

    public BoardTroops placeTroop(Troop troop, BoardPos target) {
        if (troopMap.containsKey(target)) throw new IllegalArgumentException("There already is someone on that tile");

        Map<BoardPos, TroopTile> newMap = new HashMap<>(troopMap);
        newMap.put(target, new TroopTile(troop, playingSide, TroopFace.AVERS));

        if (!isLeaderPlaced()) return new BoardTroops(playingSide, newMap, target, guards);
        if (isPlacingGuards()) return new BoardTroops(playingSide, newMap, leaderPosition, guards + 1);
        return new BoardTroops(playingSide, newMap, leaderPosition, guards);
    }

    public BoardTroops troopStep(BoardPos origin, BoardPos target) {
        if (!isLeaderPlaced() || isPlacingGuards())
            throw new IllegalStateException("Can't step yet, place leader and gurads first");
        if (!troopMap.containsKey(origin) || troopMap.containsKey(target))
            throw new IllegalArgumentException("Either origin is outside of map or target is occupied");
        Map<BoardPos, TroopTile> newMap = new HashMap<>(troopMap);
        newMap.put(target,
                   new TroopTile(troopMap.get(origin).troop, playingSide, troopMap.get(origin).flipped().face()));
        newMap.remove(origin);

        if (origin.equals(leaderPosition)) return new BoardTroops(playingSide, newMap, target, guards);
        return new BoardTroops(playingSide, newMap, leaderPosition, guards);
    }

    public BoardTroops troopFlip(BoardPos origin) {
        if (!isLeaderPlaced()) {
            throw new IllegalStateException("Cannot move troops before the leader is placed.");
        }

        if (isPlacingGuards()) {
            throw new IllegalStateException("Cannot move troops before guards are placed.");
        }

        if (!at(origin).isPresent()) throw new IllegalArgumentException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        TroopTile tile = newTroops.remove(origin);
        newTroops.put(origin, tile.flipped());

        return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
    }

    public BoardTroops removeTroop(BoardPos target) {
        if (!isLeaderPlaced() || isPlacingGuards())
            throw new IllegalStateException("Can't step yet, place leader and guards first");
        if (!troopMap.containsKey(target))
            throw new IllegalArgumentException("Either origin is outside of map or target is occupied");
        Map<BoardPos, TroopTile> newMap = new HashMap<>(troopMap);
        newMap.remove(target);
        return new BoardTroops(playingSide, newMap, target.equals(leaderPosition) ? TilePos.OFF_BOARD : leaderPosition,
                               guards);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("{\"side\":");
        playingSide.toJSON(writer);
        writer.printf(",\"leaderPosition\":");
        leaderPosition.toJSON(writer);
        writer.printf(",\"guards\":" + guards);
        writer.printf(",\"troopMap\":");
        Map<BoardPos, TroopTile> sortedMap = new TreeMap<>(Comparator.comparing(BoardPos::toString));
        sortedMap.putAll(troopMap);
        boolean first = true;
        writer.printf("{");

        for (var entry : sortedMap.entrySet()) {
            var pos = entry.getKey();
            var troop = entry.getValue();
            if (!first) writer.printf(",");
            else first = false;
            pos.toJSON(writer);
            writer.printf(":");
            troop.toJSON(writer);
        }
        writer.printf("}}");
    }
}
