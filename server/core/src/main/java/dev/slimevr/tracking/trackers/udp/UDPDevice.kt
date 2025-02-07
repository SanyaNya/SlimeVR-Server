package dev.slimevr.tracking.trackers.udp

import dev.slimevr.NetworkProtocol
import dev.slimevr.tracking.trackers.Device
import dev.slimevr.tracking.trackers.Tracker
import java.net.InetAddress
import java.net.SocketAddress
import java.util.concurrent.ConcurrentHashMap
import io.eiren.util.logging.LogManager;

class UDPDevice(
	var address: SocketAddress,
	var ipAddress: InetAddress,
	override val hardwareIdentifier: String,
	override val boardType: BoardType = BoardType.UNKNOWN,
	override val mcuType: MCUType = MCUType.UNKNOWN,
) : Device() {

	override val id: Int = nextLocalDeviceId.incrementAndGet()

	@JvmField
	var lastPacket = System.currentTimeMillis()

	@JvmField
	var lastPingPacketId = -1

	@JvmField
	var lastPingPacketTime: Long = 0
	override var name: String? = null
		set(name) {
			super.name = name
			field = name
		}

	@JvmField
	var descriptiveName: String? = null

	@JvmField
	var serialBuffer = StringBuilder()

	@JvmField
	var lastSerialUpdate: Long = 0

	@JvmField
	var lastPacketNumber: Long = -1

	@JvmField
	var protocol: NetworkProtocol? = null

	@JvmField
	var firmwareBuild = 0

	@JvmField
	var timedOut = false
	override val trackers = ConcurrentHashMap<Int, Tracker>()

	var firmwareFeatures = FirmwareFeatures()

	fun isNextPacket(packetId: Long): Boolean {
		//remove check if this is next packet
		//because packets always sequential in standard setup slime->router->pc

		//Log dropped packets
		val dropped = Math.abs(packetId - lastPacketNumber) - 1
		if(dropped > 0) LogManager.log.debug("Dropping " + dropped + " packets!")

		lastPacketNumber = packetId
		return true
	}

	override fun toString(): String = "udp:/$ipAddress"

	override var manufacturer: String?
		get() = "SlimeVR"
		set(manufacturer) {
			super.manufacturer = manufacturer
		}

	fun getTracker(id: Int): Tracker? = trackers[id]
}
