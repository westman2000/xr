package com.example.xrexp.arcore.thumbsup

import android.annotation.SuppressLint
import androidx.xr.arcore.Hand.State
import androidx.xr.arcore.HandJointType
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Vector3

/**
 * Detects whether a hand is showing a "thumbs up" gesture based on hand tracking data.
 */
object ThumbsUpDetector {

    /**
     * Determines if a hand is showing a "thumbs up" gesture.
     *
     * @param handState The state of the hand to check
     * @return true if the hand is showing a thumbs up gesture, false otherwise
     */
    @SuppressLint("RestrictedApi")
    fun isThumbsUp(handState: State): Boolean {
        // Check if the hand is being tracked
        if (!handState.isActive) {
            return false
        }

        val joints = handState.handJoints

        // Get all required joints
        val wristPose = joints[HandJointType.WRIST] ?: return false
        val palmPose = joints[HandJointType.PALM] ?: return false

        // Thumb joints
        val thumbMetacarpalPose = joints[HandJointType.THUMB_METACARPAL] ?: return false
        val thumbTipPose = joints[HandJointType.THUMB_TIP] ?: return false

        // Fingertips
        val indexTipPose = joints[HandJointType.INDEX_TIP] ?: return false
        val middleTipPose = joints[HandJointType.MIDDLE_TIP] ?: return false
        val ringTipPose = joints[HandJointType.RING_TIP] ?: return false
        val littleTipPose = joints[HandJointType.LITTLE_TIP] ?: return false

        // Finger intermediate joints
        val indexIntermediatePose = joints[HandJointType.INDEX_INTERMEDIATE] ?: return false
        val middleIntermediatePose = joints[HandJointType.MIDDLE_INTERMEDIATE] ?: return false
        val ringIntermediatePose = joints[HandJointType.RING_INTERMEDIATE] ?: return false
        val littleIntermediatePose = joints[HandJointType.LITTLE_INTERMEDIATE] ?: return false

        // 1. Establish hand coordinate system

        // Forward vector: from wrist to palm
        val forwardVec = createVector(wristPose.translation, palmPose.translation)
        val forwardVecNorm = normalize(forwardVec)

        // Side vector (approximation)
        val indexMetacarpalPose = joints[HandJointType.INDEX_METACARPAL] ?: return false
        val sideVec = createVector(indexMetacarpalPose.translation, thumbMetacarpalPose.translation)

        // Calculate the "up" direction using cross product
        val upVec = normalize(crossProduct(forwardVecNorm, normalize(sideVec)))

        // 2. Calculate thumb direction (from metacarpal to tip)
        val thumbVec = createVector(thumbMetacarpalPose.translation, thumbTipPose.translation)
        val thumbVecNorm = normalize(thumbVec)

        // 3. Check if thumb is pointing up relative to hand
        val thumbUpAlignment = dotProduct(thumbVecNorm, upVec)
        val isThumbPointingUp = thumbUpAlignment > 0.7f // Threshold can be adjusted

        // 4. Check thumb extension
        val thumbMetaToWristDist = distance(thumbMetacarpalPose.translation, wristPose.translation)
        val thumbTipToWristDist = distance(thumbTipPose.translation, wristPose.translation)
        val isThumbExtended = thumbTipToWristDist > thumbMetaToWristDist * 1.5f

        // 5. Check if other fingers are curled
        val isIndexCurled = isFingerCurled(joints, HandJointType.INDEX_PROXIMAL,
            HandJointType.INDEX_INTERMEDIATE, HandJointType.INDEX_TIP)
        val isMiddleCurled = isFingerCurled(joints, HandJointType.MIDDLE_PROXIMAL,
            HandJointType.MIDDLE_INTERMEDIATE, HandJointType.MIDDLE_TIP)
        val isRingCurled = isFingerCurled(joints, HandJointType.RING_PROXIMAL,
            HandJointType.RING_INTERMEDIATE, HandJointType.RING_TIP)
        val isLittleCurled = isFingerCurled(joints, HandJointType.LITTLE_PROXIMAL,
            HandJointType.LITTLE_INTERMEDIATE, HandJointType.LITTLE_TIP)

        // For thumbs up, thumb should be extended and pointing up, and other fingers should be curled
        return isThumbExtended &&
                isThumbPointingUp &&
                isIndexCurled &&
                isMiddleCurled &&
                isRingCurled &&
                isLittleCurled
    }

    /**
     * Determines if a finger is curled by examining the angle between its segments.
     */
    @SuppressLint("RestrictedApi")
    private fun isFingerCurled(joints: Map<HandJointType, Pose>,
                               proximalType: HandJointType,
                               intermediateType: HandJointType,
                               tipType: HandJointType): Boolean {

        val proximalPose = joints[proximalType] ?: return false
        val intermediatePose = joints[intermediateType] ?: return false
        val tipPose = joints[tipType] ?: return false

        // Vector from proximal to intermediate
        val segmentA = createVector(proximalPose.translation, intermediatePose.translation)

        // Vector from intermediate to tip
        val segmentB = createVector(intermediatePose.translation, tipPose.translation)

        // Calculate the dot product between the two segments
        val dotProd = dotProduct(normalize(segmentA), normalize(segmentB))

        // For a curled finger, the dot product should be low (segments are not aligned)
        // Dot product < 0.7 corresponds to an angle greater than about 45 degrees
        return dotProd < 0.7f
    }

    /**
     * Creates a vector from point1 to point2.
     */
    private fun createVector(point1: Vector3, point2: Vector3): Vector3 {
        return Vector3(
            point2.x - point1.x,
            point2.y - point1.y,
            point2.z - point1.z
        )
    }

    /**
     * Normalizes a vector to unit length.
     */
    private fun normalize(v: Vector3): Vector3 {
        val length = Math.sqrt((v.x * v.x + v.y * v.y + v.z * v.z).toDouble()).toFloat()
        return if (length > 0.000001f) {
            Vector3(v.x / length, v.y / length, v.z / length)
        } else {
            Vector3(0f, 0f, 0f)
        }
    }

    /**
     * Calculates the dot product of two vectors.
     */
    private fun dotProduct(v1: Vector3, v2: Vector3): Float {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z
    }

    /**
     * Calculates the cross product of two vectors.
     */
    private fun crossProduct(v1: Vector3, v2: Vector3): Vector3 {
        return Vector3(
            v1.y * v2.z - v1.z * v2.y,
            v1.z * v2.x - v1.x * v2.z,
            v1.x * v2.y - v1.y * v2.x
        )
    }

    /**
     * Calculates the Euclidean distance between two points in 3D space.
     */
    private fun distance(point1: Vector3, point2: Vector3): Float {
        val dx = point2.x - point1.x
        val dy = point2.y - point1.y
        val dz = point2.z - point1.z
        return Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toFloat()
    }
}