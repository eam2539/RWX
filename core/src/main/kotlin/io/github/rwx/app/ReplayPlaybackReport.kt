package io.github.rwx.app

import java.util.*

/**
 * Platform-neutral Track B value type describing the outcome of replaying a single recorded
 * command frame. [expectedStateHash] is the hash captured when the frame was recorded;
 * [actualStateHash] is the hash recomputed from the live session after the frame's commands were
 * re-applied through the authoritative runtime command path. A null expected hash means the frame
 * was recorded without verification and is therefore treated as an unverifiable match.
 */
data class ReplayFramePlayback(
    val tick: Long,
    val expectedStateHash: Long?,
    val actualStateHash: Long?,
) {
    val matches: Boolean get() = expectedStateHash == null || expectedStateHash == actualStateHash

    val verified: Boolean get() = expectedStateHash != null
}

/**
 * Platform-neutral Track B report aggregating the per-frame outcome of a replay playback. The
 * report only observes recomputed state hashes; it never reimplements gameplay rules. Frames are
 * defensively copied and kept in playback order.
 */
class ReplayPlaybackReport(frames: List<ReplayFramePlayback> = emptyList()) {
    val frames: List<ReplayFramePlayback> = Collections.unmodifiableList(frames.toList())

    val isConsistent: Boolean get() = frames.all { it.matches }

    val firstDivergence: ReplayFramePlayback? get() = frames.firstOrNull { !it.matches }

    val verifiedFrameCount: Int get() = frames.count { it.verified }

    override fun equals(other: Any?): Boolean = other is ReplayPlaybackReport && frames == other.frames

    override fun hashCode(): Int = frames.hashCode()

    override fun toString(): String = "ReplayPlaybackReport(frames=$frames)"
}
