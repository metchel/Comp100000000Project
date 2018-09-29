#!/bin/bash

#TODO: SPECIFY THE HOSTNAMES OF 4 CS MACHINES (lab1-1, cs-2, etc...)
MACHINES=(oclark2@lab2-15.cs.mcgill.ca oclark2@lab2-17.cs.mcgill.ca oclark2@lab2-19.cs.mcgill.ca oclark2@lab2-21.cs.mcgill.ca)

tmux new-session \; \
	split-window -h \; \
	split-window -v \; \
	split-window -v \; \
	select-layout main-vertical \; \
	select-pane -t 2 \; \
	send-keys "ssh -t ${MACHINES[0]} \"cd Comp100000000Project/src/Server/ > /dev/null; echo -n 'Connected to '; hostname; ls -l; pwd; ./run_server.sh Flights\"" C-m \; \
	select-pane -t 3 \; \
	send-keys "ssh -t ${MACHINES[1]} \"cd Comp100000000Project/src/Server/ > /dev/null; echo -n 'Connected to '; hostname; ls -l; pwd;   ./run_server.sh Cars\"" C-m \; \
	select-pane -t 1 \; \
	send-keys "ssh -t ${MACHINES[2]} \"cd Comp100000000Project/src/Server/ > /dev/null; echo -n 'Connected to '; hostname; ls -l; pwd; ./run_server.sh Rooms\"" C-m \; \
	select-pane -t 0 \; \
	send-keys "ssh -t ${MACHINES[3]} \"cd Comp100000000Project/src/Server/ > /dev/null; echo -n 'Connected to '; hostname; ls -l; pwd;  sleep .5s; ./run_middleware.sh ${MACHINES[0]} ${MACHINES[1]} ${MACHINES[2]}\"" C-m \;
    