#!/bin/bash
#TODO: SPECIFY THE HOSTNAMES OF 4 CS MACHINES (lab1-1, cs-2, etc...)
MACHINES=(lab1-6.cs.mcgill.ca lab1-8.cs.mcgill.ca lab1-9.cs.mcgill.ca lab1-2.cs.mcgill.ca lab2-3.cs.mcgill.ca)
USER="metche@"
tmux new-session \; \
	split-window -h \; \
	split-window -h \; \
	split-window -v \; \
	split-window -v \; \
	select-layout main-vertical \; \
	select-pane -t 0 \; \
	send-keys "ssh -t $USER${MACHINES[0]} \"cd Comp100000000Project/src/Server/ > /dev/null; echo -n 'Connected to '; hostname; ls -l; pwd; ./run_tcp_server.sh \"" C-m \; \
	select-pane -t 1 \; \
	send-keys "ssh -t $USER${MACHINES[1]} \"cd Comp100000000Project/src/Server/ > /dev/null; echo -n 'Connected to '; hostname; ls -l; pwd;   ./run_tcp_server.sh \"" C-m \; \
	select-pane -t 2 \; \
	send-keys "ssh -t $USER${MACHINES[2]} \"cd Comp100000000Project/src/Server/ > /dev/null; echo -n 'Connected to '; hostname; ls -l; pwd; ./run_tcp_server.sh \"" C-m \; \
	select-pane -t 3 \; \
	send-keys "ssh -t $USER${MACHINES[3]} \"cd Comp100000000Project/src/Server/ > /dev/null; echo -n 'Connected to '; hostname; ls -l; pwd; ./run_tcp_server.sh \"" C-m \; \
	select-pane -t 4 \; \
	send-keys "ssh -t $USER${MACHINES[4]} \"cd Comp100000000Project/src/Server/ > /dev/null; echo -n 'Connected to '; hostname; ls -l; pwd; sleep .5s; ./run_tcp_middleware.sh ${MACHINES[0]} ${MACHINES[1]} ${MACHINES[2]} ${MACHINES[3]}\"" C-m \;
