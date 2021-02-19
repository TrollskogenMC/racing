package com.github.hornta.racing.objects;

import java.util.UUID;

public class PlayerSessionResult {
	private final UUID playerId;
	private final int lastLap;
	private final RaceCheckpoint lastCheckpoint;
	private final long raceDuration;
	private final boolean completedRace;
	private final long fastestLapDuration;

	PlayerSessionResult(UUID playerId, long raceDuration, int lastLap, RaceCheckpoint lastCheckpoint, boolean completedRace, long fastestLapDuration) {
		this.playerId = playerId;
		this.raceDuration = raceDuration;
		this.lastLap = lastLap;
		this.lastCheckpoint = lastCheckpoint;
		this.completedRace = completedRace;
		this.fastestLapDuration = fastestLapDuration;
	}

	public long getFastestLapDuration() {
		return fastestLapDuration;
	}

	public boolean hasCompletedRace() {
		return completedRace;
	}

	public RaceCheckpoint getLastCheckpoint() {
		return lastCheckpoint;
	}

	public int getLastLap() {
		return lastLap;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public long getRaceDuration() {
		return raceDuration;
	}
}
