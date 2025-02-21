import os
import re
import glob

#base_dir1 = "logs/exec_4MB"
base_dir = "logs/exec_*" 

def parse_sar_log(file_path):



    cpu_data = []
    disk_data = [];mem_data = []; net_data = []

    with open(file_path, "r") as file:
        for line in file:
            # cpu_match = re.search(r"(\d{2}:\d{3})\s+all\)\s+[\d.]+\s+([\d.]+)\s+([\d.]+)\s+[\d.]+\s+([\d.]+)", stats)
            cpu_match = re.search(r"(\d{2}:\d{2}:\d{2})\s+all\s+([\d.]+)\s+[\d.]+\s+([\d.]+)\s+([\d.]+)\s+[\d.]+\s+([\d.]+)", line)
            if cpu_match:
                time, user, system, iowait, idle = cpu_match.groups()
                cpu_data.append((float(user), float(system), float(iowait), float(idle)))

             # disk_match = re.search(r"(\d{2}:\d{2}:\d{3}\)s+(\d+\.\d+)\s+(\d+\.\d+)\s+(\d+\.\d+)", stats)
            disk_match = re.search(r"(\d{2}:\d{2}:\d{2})\s+(\d+\.\d+)\s+(\d+\.\d+)\s+(\d+\.\d+)\s+(\d+\.\d+)", line)
            if disk_match:
                _, tps, rtps, wtps, _ = disk_match.groups()
                disk_data.append((float(tps), float(rtps), float(wtps)))



            mem_match = re.search(r"(\d{2}:\d{2}:\d{2})\s+(\d+)\s+(\d+)\s+(\d+)\s+([\d.]+)", line)
            if mem_match:
                _, _, _, kbused, _ = mem_match.groups()
                mem_data.append(int(kbused))

            net_match = re.search(r"(\d{2}:\d{2}:\d{2})\s+\w+\s+([\d.]+)\s+([\d.]+)\s+([\d.]+)\s+([\d.]+)", line)
            if net_match:
                _, _, _, rxkb, txkb = net_match.groups()
                net_data.append((float(rxkb), float(txkb)))




    return cpu_data, disk_data, mem_data, net_data


def parse_time_log(file_path):



    time_stats = {}

    with open(file_path, "r") as file:

        for line in file:


            if "User time" in line:
                #time_stats["User Time"] = float(re.search(r"([\d.]+)", line))
                time_stats["User Time"] = float(re.search(r"([\d.]+)", line).group())

            elif "System time" in line:
                # time_stats["System Time"] = float(re.search(r"([\d.]+)", line))
                time_stats["System Time"] = float(re.search(r"([\d.]+)", line).group())

            elif "Elapsed" in line:

                elapsed_match = re.search(r"(\d+:)?(\d+):([\d.]+)", line)
                if elapsed_match:
                    minutes = int(elapsed_match.group(2))
                    seconds = float(elapsed_match.group(3))
                    total_seconds = minutes * 60 + seconds
                    time_stats["Elapsed Time (s)"] = total_seconds

            # Note to self --> not needed --> maximum resident set size is not used for analysis
            elif "Maximum resident set size" in line:
                time_stats["Max Memory (MB)"] = int(re.search(r"(\d+)", line).group()) / 1024

    return time_stats




def generate_summary(cpu_data, disk_data, mem_data, net_data, time_data):
    summary = {}

    if cpu_data:
        avg_cpu_user = sum(x[0] for x in cpu_data) / len(cpu_data)
        avg_cpu_system = sum(x[1] for x in cpu_data) / len(cpu_data)
        avg_cpu_iowait = sum(x[2] for x in cpu_data) / len(cpu_data)
        avg_cpu_idle = sum(x[3] for x in cpu_data) / len(cpu_data)
        # print(avg_cpu_user, avg_cpu_system, avg_cpu_iowait, avg_cpu_idle)
        summary["CPU Usage"] = f"User: {avg_cpu_user:.2f}%, System: {avg_cpu_system:.2f}%, IO Wait: {avg_cpu_iowait:.2f}%, Idle: {avg_cpu_idle:.2f}%"

    if disk_data:
        avg_tps = sum(x[0] for x in disk_data) / len(disk_data)
        avg_rtps = sum(x[1] for x in disk_data) / len(disk_data)
        avg_wtps = sum(x[2] for x in disk_data) / len(disk_data)
        # print(avg_tps, avg_rtps, avg_wtps)
        summary["Disk Usage"] = f"TPS: {avg_tps:.2f}, Read TPS: {avg_rtps:.2f}, Write TPS: {avg_wtps:.2f}"

    if mem_data:
        avg_mem_used = sum(mem_data) / len(mem_data)
        # print(avg_mem_used)
        summary["Memory Usage"] = f"Avg Used Memory: {avg_mem_used / 1024:.2f} MB"

    if net_data:
        avg_rxkb = sum(x[0] for x in net_data) / len(net_data)
        avg_txkb = sum(x[1] for x in net_data) / len(net_data)
        # print(avg_rxkb, avg_txkb)
        summary["Network Usage"] = f"RX: {avg_rxkb:.2f} KB/s, TX: {avg_txkb:.2f} KB/s"

    if time_data:
        # print(time_data)
        summary.update(time_data)

    return summary

def process_all_logs():
    # note to self --> sometimes fails
    log_dirs = glob.glob(os.path.join(base_dir, "*", "metrics"))

    for log_dir in log_dirs:
        sar_log = os.path.join(log_dir, "sar.log")
        time_log = os.path.join(log_dir, "time.txt")

        if os.path.exists(sar_log):
            cpu_data, disk_data, mem_data, net_data = parse_sar_log(sar_log)
        else:
            cpu_data, disk_data, mem_data, net_data = [], [], [], []

        if os.path.exists(time_log):
            time_data = parse_time_log(time_log)
        else:
            time_data = {}

        summary = generate_summary(cpu_data, disk_data, mem_data, net_data, time_data)

        print(f"\nProcessing: {log_dir}")
        print("\nSummary Report:")
        for key, value in summary.items():
            print(f"{key}: {value}")

if __name__ == "__main__":
    process_all_logs()
