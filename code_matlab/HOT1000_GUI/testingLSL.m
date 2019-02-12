clear data*

cwd = cd;                                                                   % Current working directory
addpath(genpath(sprintf('%s\liblsl-Matlab',cwd)));                          % Add library path

% Kill the terminal
dos('taskkill /F /T /IM cmd.exe'); 

% Run the nirstreamer.jar in background
jarfile = 'NIRStreamer-20161012.jar';
commandtext = sprintf('java -jar %s -c %s &', jarfile);
system(commandtext);  

% instantiate the library
disp('Loading the library ....');
lib = lsl_loadlib();

% resolve a stream 
disp('Resolving an EEG stream ...');
result = {};
while isempty(result)
    %result = lsl_resolve_byprop(lib,'type','Audio');
    result = lsl_resolve_byprop(lib,'type','Audio');
    
end

% create a new inlet
disp('Opening an inlet ...')
inlet = lsl_inlet(result{1});

disp('Now receiving chunked data...');
while true
    %get chunk from the inlet
    [chunk,stamps] = inlet.pull_chunk();
    for s=1:lenght(stamps)
        % and display it
        fprintf('%.2f\t',chunk(:,s));
        fprintf('%.5f\n',stamps(s));
    end
    pause(0.05); 
end

      