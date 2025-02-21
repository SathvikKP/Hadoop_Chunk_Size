import os
import re
import glob

base_dir = "logs/exec_*"  # Adjust if needed

def parse_sar_log(file_path):
    """Reads a sar.log file and extracts system stats."""
    cpu_data = []
    disk_data = []
    mem_data = []
    net_data = []

    with open(file_path, "r") as file:
        for line in file:
            # CPU Usage (time, user%, system%, iowait%, idle%)
            cpu_match = re.search(r"(\d{2}:\d{2}:\d{2})\s+all\s+([\d.]+)\s+[\d.]+\s+([\d.]+)\s+([\d.]+)\s+[\d.]+\s+([\d.]+)", line)
            if cpu_match:
                time, user, system, iowait, idle = cpu_match.groups()
                cpu_data.append((time, float(user), float(system), float(iowait), float(idle)))

            # Disk Usage (time, tps, read tps, write tps, read speed)
            disk_match = re.search(r"(\d{2}:\d{2}:\d{2})\s+(\d+\.\d+)\s+(\d+\.\d+)\s+(\d+\.\d+)\s+(\d+\.\d+)", line)
            if disk_match:
                time, tps, rtps, wtps, bread = disk_match.groups()
                disk_data.append((time, float(tps), float(rtps), float(wtps), float(bread)))

            # Memory Usage (time, free mem, available mem, used mem, percent used)
            mem_match = re.search(r"(\d{2}:\d{2}:\d{2})\s+(\d+)\s+(\d+)\s+(\d+)\s+([\d.]+)", line)
            if mem_match:
                time, kbfree, kbavail, kbused, memused = mem_match.groups()
                mem_data.append((time, int(kbfree), int(kbavail), int(kbused), float(memused)))

            # Network Usage (time, packets in, packets out, rx KB/s, tx KB/s)
            net_match = re.search(r"(\d{2}:\d{2}:\d{2})\s+\w+\s+([\d.]+)\s+([\d.]+)\s+([\d.]+)\s+([\d.]+)", line)
            if net_match:
                time, rxpck, txpck, rxkb, txkb = net_match.groups()
                net_data.append((time, float(rxpck), float(txpck), float(rxkb), float(txkb)))

    return cpu_data, disk_data, mem_data, net_data

def generate_summary(cpu_data, disk_data, mem_data, net_data):
    """Generates a summary of system performance."""
    summary = {}

    if cpu_data:
        avg_cpu_user = sum(x[1] for x in cpu_data) / len(cpu_data)
        avg_cpu_system = sum(x[2] for x in cpu_data) / len(cpu_data)
        avg_cpu_iowait = sum(x[3] for x in cpu_data) / len(cpu_data)
        avg_cpu_idle = sum(x[4] for x in cpu_data) / len(cpu_data)
        summary["CPU Usage"] = f"User: {avg_cpu_user:.2f}%, System: {avg_cpu_system:.2f}%, IO Wait: {avg_cpu_iowait:.2f}%, Idle: {avg_cpu_idle:.2f}%"

    if disk_data:
        avg_tps = sum(x[1] for x in disk_data) / len(disk_data)
        avg_rtps = sum(x[2] for x in disk_data) / len(disk_data)
        avg_wtps = sum(x[3] for x in disk_data) / len(disk_data)
        summary["Disk Usage"] = f"TPS: {avg_tps:.2f}, Read TPS: {avg_rtps:.2f}, Write TPS: {avg_wtps:.2f}"

    if mem_data:
        avg_mem_used = sum(x[3] for x in mem_data) / len(mem_data)
        summary["Memory Usage"] = f"Avg Used Memory: {avg_mem_used / 1024:.2f} MB"

    if net_data:
        avg_rxkb = sum(x[3] for x in net_data) / len(net_data)
        avg_txkb = sum(x[4] for x in net_data) / len(net_data)
        summary["Network Usage"] = f"RX: {avg_rxkb:.2f} KB/s, TX: {avg_txkb:.2f} KB/s"

    return summary

def process_all_logs():
    """Finds and processes all sar.log files."""
    log_files = glob.glob(os.path.join(base_dir, "*", "metrics", "sar.log"))

    for log_file in log_files:
        print(f"\nProcessing: {log_file}")
        cpu_data, disk_data, mem_data, net_data = parse_sar_log(log_file)
        summary = generate_summary(cpu_data, disk_data, mem_data, net_data)

        print("\nSummary Report:")
        for key, value in summary.items():
            print(f"{key}: {value}")

if __name__ == "__main__":
    process_all_logs()
