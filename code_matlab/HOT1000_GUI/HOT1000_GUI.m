function varargout = HOT1000_GUI(varargin)
% HOT1000_GUI MATLAB code for HOT1000_GUI.fig

%Access the pusher directory
%cd STAGE_UTP\5_Research\HOT1000_HitachiSensorV3\HOT1000_GUI\memory-pusher-master

clear data*

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
    'gui_Singleton',  gui_Singleton, ...
    'gui_OpeningFcn', @HOT1000_GUI_OpeningFcn, ...
    'gui_OutputFcn',  @HOT1000_GUI_OutputFcn, ...
    'gui_LayoutFcn',  [] , ...
    'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end

% --- Executes just before HOT1000_GUI is made visible.
function HOT1000_GUI_OpeningFcn(hObject, eventdata, handles, varargin)

handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% --- Outputs from this function are returned to the command line.
function varargout = HOT1000_GUI_OutputFcn(hObject, eventdata, handles)

% Get default command line output from handles structure
varargout{1} = handles.output;

set(hObject, 'units','normalized','outerposition',[0 0 1 1]);   % Maximize GUI
movegui(hObject,'center');                                      % Center GUI

setappdata(handles.HOT1000_GUI,'ori_ins',get(handles.text_ins,'String'));   % Save instructions
setappdata(handles.HOT1000_GUI,'record_state',0);                           % Set recording status
cwd = cd;                                                                   % Current working directory
addpath(genpath(sprintf('%s\liblsl-Matlab',cwd)));                          % Add library path
listbox_comport_Callback(hObject, eventdata, handles);                      % Calling listbox function
drawnow;

% Callback function for com port listbox
function listbox_comport_Callback(hObject, eventdata, handles)

available_comport = seriallist;                                 % Identify available com ports
set(handles.listbox_comport,'String',available_comport);        % Display those ports
setappdata(handles.HOT1000_GUI,'com_port', ...
    available_comport(get(handles.listbox_comport,'Value')));   % Detect which port is chose

if isempty(getappdata(handles.HOT1000_GUI,'com_port')) == 0     % If any port is chosen
    set(handles.pushbutton_ble,'Enable','on');                  % Enable scan button
    set(handles.pushbutton_ble,'String',sprintf('Scan for Device(s)'));
else                                                            % If no port is chosen
    set(handles.pushbutton_ble,'Enable','off');                 % Disable scan button
    set(handles.pushbutton_ble,'String','Scan for Device(s)');
end
drawnow;

% Executes during com port listbox creation
function listbox_comport_CreateFcn(hObject, eventdata, handles)

if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% Callback function for scan button
function pushbutton_ble_Callback(hObject, eventdata, handles)

for i = 1:6     % Disable and hide all 6 check boxes
    eval(sprintf('set(handles.checkbox%d,''Enable'',''off'');',i));
    eval(sprintf('set(handles.checkbox%d,''Visible'',''off'');',i));
    eval(sprintf('set(handles.checkbox%d,''Value'',0);',i));
end

set(handles.uipanel_plot,'Visible','off');          % Hide real time plot
setappdata(handles.HOT1000_GUI,'record_state',0);   % Update recording status
set(handles.uipanel_ins,'Title','Status');          % Change title Instructions to title Status
set(handles.text_ins,'String','');                  % Empty the string
drawnow;

dos('taskkill /F /T /IM cmd.exe');                  % Kill any running cmd

if isappdata(handles.HOT1000_GUI,'browser')         % Close if opened
    close(getappdata(handles.HOT1000_GUI,'browser'));
end

set(handles.pushbutton_ble,'String','Scanning for Device(s)...');
drawnow;
com_port = getappdata(handles.HOT1000_GUI,'com_port');      % Acquire the port chosen

% Run the nirstreamer.jar in background
jarfile = 'NIRStreamer-20161012.jar';
commandtext = sprintf('java -jar %s -c %s &', jarfile, com_port);
system(commandtext);

% Pushing data
system('node E:\STAGE_UTP\5_Research\HOT1000_HitachiSensorV2\HOT1000_GUI\memory-pusher-master/memory3.js');

tmp_time = datetime;                                        % Get date & time
for i = 1:5                                                 % Update status while nirstreamer is running
    set(handles.text_ins,'String',sprintf('%s NIRStreamer job is running.',tmp_time));
    drawnow;
    pause(0.3);
    set(handles.text_ins,'String',sprintf('%s NIRStreamer job is running..',tmp_time));
    drawnow;
    pause(0.3);
    set(handles.text_ins,'String',sprintf('%s NIRStreamer job is running...',tmp_time));
    drawnow;
    pause(0.3);
end

% Update status
tmp = get(handles.text_ins,'String');
tmp = tmp(1:48);
set(handles.text_ins,'String',tmp);
tmp = {get(handles.text_ins,'String')};
tmp{2,:} = sprintf('%s NIRStreamer job has finished running.',datetime);
set(handles.text_ins,'String',tmp);
drawnow;

% Update status
tmp_time = datetime;
tmp{3,:} = sprintf('%s Loading lab streaming layer library...',tmp_time);
set(handles.text_ins,'String',tmp);
drawnow;

lib = lsl_loadlib();        % Load library
tmp{3,:} = sprintf('%s Finished loading lab streaming layer library.',tmp_time);
tmp_time = datetime;
tmp{4,:} = sprintf('%s Accessing localhost:9876...',tmp_time);
set(handles.text_ins,'String',tmp);
drawnow;

[stat,browser] = web('http://127.0.0.1:9876/','-new','-notoolbar');     % Open HOT1000 controller (localhost:9876)
setappdata(handles.HOT1000_GUI,'browser',browser);                      % Save as appdata
tmp{4,:} = sprintf('%s Successfully access localhost 9876.',tmp_time);
set(handles.text_ins,'String',tmp);
drawnow;

% Update status
tmp_time = datetime;
tmp = get(handles.text_ins,'String');
tmp{5,:} = sprintf('%s Searching for HOT1000 device(s)...',tmp_time);
set(handles.text_ins,'String',tmp);
drawnow;

% Search and filter for HOT1000 devices
[hot1000_device_no hot1000_lsl_streams] = hot_1000_search(lib,handles);
setappdata(handles.HOT1000_GUI,'hot1000_device_no',hot1000_device_no);
setappdata(handles.HOT1000_GUI,'hot1000_lsl_streams',hot1000_lsl_streams);

% Update status
set(handles.pushbutton_ble,'String','Scan for Device');
set(handles.pushbutton_ble,'Value',0);
set(handles.HOT1000_GUI,'Visible','off');
drawnow;
set(handles.HOT1000_GUI,'Visible','on');
drawnow;

for i = 1:hot1000_device_no                     % Enable and show checkboxes based on number of devices
    pos = get(handles.checkbox1,'Position');
    pos(2) = pos(2) - (i-1)*0.0962;
    eval(sprintf('set(handles.checkbox%d,''Position'',pos);',i))
    eval(sprintf('set(handles.checkbox%d,''Enable'',''on'');',i))
    eval(sprintf('set(handles.checkbox%d,''Visible'',''on'');',i));
end
drawnow;

% Enable data recording
setappdata(handles.HOT1000_GUI,'scan_valid',1);
maincheckbox_Callback(hObject, eventdata, handles);


% Executes during closing GUI
function HOT1000_GUI_DeleteFcn(hObject, eventdata, handles)

close(getappdata(handles.HOT1000_GUI,'browser'));   % Close the web browser
dos('taskkill /F /T /IM cmd.exe');                  % Kill any running cmd



% Executes on reset button press.
function pushbutton_reset_Callback(hObject, eventdata, handles)

% Recover original status
set(handles.uipanel_ins,'Title','Instructions');
setappdata(handles.HOT1000_GUI,'record_state',0);
set(handles.uipanel_plot,'Visible','off');
set(handles.pushbutton_ble,'String','Scan for Device(s)');
set(handles.text_ins,'String',getappdata(handles.HOT1000_GUI,'ori_ins'));
set(handles.edit_id,'String','');
set(handles.togglebutton_record,'Enable','off');
set(handles.togglebutton_record,'Visible','off');
drawnow;

% Remove relevant app data
if isappdata(handles.HOT1000_GUI,'scan_valid')
    rmappdata(handles.HOT1000_GUI,'scan_valid');
end

if isappdata(handles.HOT1000_GUI,'hot1000_device_no')
    rmappdata(handles.HOT1000_GUI,'hot1000_device_no');
end

if isappdata(handles.HOT1000_GUI,'hot1000_lsl_streams')
    rmappdata(handles.HOT1000_GUI,'hot1000_lsl_streams');
end

if isappdata(handles.HOT1000_GUI,'browser')
    close(getappdata(handles.HOT1000_GUI,'browser'));
end

dos('taskkill /F /T /IM cmd.exe');                  % Kill any running cmd

close(getappdata(handles.HOT1000_GUI,'browser'));

% Hide all checkboxes
for i = 1:6
    eval(sprintf('set(handles.checkbox%d,''Enable'',''off'');',i));
    eval(sprintf('set(handles.checkbox%d,''Visible'',''off'');',i));
    eval(sprintf('set(handles.checkbox%d,''Value'',0);',i));
end
drawnow;

% Executes when any of the checkboxes is pressed.
function maincheckbox_Callback(hObject, eventdata, handles)

% Get details of connected devices
hot1000_device_no = getappdata(handles.HOT1000_GUI,'hot1000_device_no');
hot1000_lsl_streams = getappdata(handles.HOT1000_GUI,'hot1000_lsl_streams');

% Check which checkbox is ticked
checkbox_mask = [get(handles.checkbox1,'Value') get(handles.checkbox2,'Value') ...
    get(handles.checkbox3,'Value') get(handles.checkbox4,'Value') ...
    get(handles.checkbox5,'Value') get(handles.checkbox6,'Value')];
setappdata(handles.HOT1000_GUI,'checkbox_mask',checkbox_mask);

% If any checkbox is checked and session ID is input and valid scanning is done
if isempty(find(checkbox_mask == 1)) ~= 1 && isempty(get(handles.edit_id,'String')) ~= 1 ...
        && isempty(getappdata(handles.HOT1000_GUI,'scan_valid')) ~= 1
    set(handles.togglebutton_record,'Enable','on');
    set(handles.togglebutton_record,'Visible','on');
    tmp = get(handles.text_ins,'String');
    x = tmp{2};
    x(39) = sprintf('%d',length(find(checkbox_mask == 1)));
    tmp{2} =x ;
    set(handles.text_ins,'String',tmp);
    drawnow;
    
    % If no checkbox is checked and session ID is input and valid scanning is done
elseif isempty(find(checkbox_mask == 1)) == 1 && isempty(get(handles.edit_id,'String')) ~= 1 ...
        && isempty(getappdata(handles.HOT1000_GUI,'scan_valid')) ~= 1
    set(handles.togglebutton_record,'Enable','off');
    set(handles.togglebutton_record,'Visible','off');
    tmp = get(handles.text_ins,'String');
    x = tmp{2};
    x(39) = sprintf('%d',length(find(checkbox_mask == 1)));
    tmp{2} =x ;
    set(handles.text_ins,'String',tmp);
    drawnow;
    
    % If any checkbox is checked and no session ID is input and valid scanning is done
elseif isempty(find(checkbox_mask == 1)) ~= 1 && isempty(get(handles.edit_id,'String')) == 1 ...
        && isempty(getappdata(handles.HOT1000_GUI,'scan_valid')) ~= 1
    set(handles.togglebutton_record,'Enable','off');
    set(handles.togglebutton_record,'Visible','off');
    tmp = get(handles.text_ins,'String');
    x = tmp{2};
    x(39) = sprintf('%d',length(find(checkbox_mask == 1)));
    tmp{2} = x ;
    set(handles.text_ins,'String',tmp);
    drawnow;
    
    % In other conditions
else
    set(handles.togglebutton_record,'Enable','off');
    set(handles.togglebutton_record,'Visible','off');
end
drawnow;

% Executes during the checkboxes are pressed.
function checkbox1_Callback(hObject, eventdata, handles)
maincheckbox_Callback(hObject, eventdata, handles);

function checkbox2_Callback(hObject, eventdata, handles)
maincheckbox_Callback(hObject, eventdata, handles);

function checkbox3_Callback(hObject, eventdata, handles)
maincheckbox_Callback(hObject, eventdata, handles);

function checkbox4_Callback(hObject, eventdata, handles)
maincheckbox_Callback(hObject, eventdata, handles);

function checkbox5_Callback(hObject, eventdata, handles)
maincheckbox_Callback(hObject, eventdata, handles);

function checkbox6_Callback(hObject, eventdata, handles)
maincheckbox_Callback(hObject, eventdata, handles);

% Executes when the recording toggle button is pressed.
function togglebutton_record_Callback(hObject, eventdata, handles)

% Update status
tmp = get(handles.text_ins,'String');
tmp_date = datetime;
tmp{1} = sprintf('%s Recording started. Push start on the device(s).',tmp_date);
set(handles.text_ins,'String',tmp);
drawnow;

% Get device info
hot1000_device_no = getappdata(handles.HOT1000_GUI,'hot1000_device_no');
hot1000_lsl_streams = getappdata(handles.HOT1000_GUI,'hot1000_lsl_streams');
checkbox_mask = getappdata(handles.HOT1000_GUI,'checkbox_mask');

% Disable the checkboxes
for i = 1:hot1000_device_no
    eval(sprintf('set(handles.checkbox%d,''Enable'',''off'');',i))
end

% Change button string
set(handles.togglebutton_record,'String','Stop Recording');
drawnow;

device_id_list = {};                                % Create empty string array
checkbox_loc = find(checkbox_mask==1);              % Create an array of 0 and 1, based on which device is checked
hot1000_device_valid = length(checkbox_loc);        % Determine the number of checked devices
all_subplot_loc = [2 1; 1 2; 1 3; 2 2; 2 3; 2 3];   % Pre-determined subplot locations (max 6 plots/devices)
window_size = 100;                                  % Plot size of 100 data points

for i = 1:hot1000_device_valid                                              % For each checked device
    tmp_name = hot1000_lsl_streams{checkbox_loc(i)}.source_id;              % Getting the device id
    device_id_list{end+1} = tmp_name(end-1:end);                            % Getting the last two digit of the MAC address
    eval(sprintf('inlet_%s = lsl_inlet(hot1000_lsl_streams{%d});', ...      % Opening a channel to stream the data, based on the device id
        device_id_list{i},checkbox_loc(i)));
    eval(sprintf('clear data_%s;',device_id_list{i}));                      % Clear the variable to store the data
    
    if getappdata(handles.HOT1000_GUI,'record_state') == 0                  % If no recording was done
        
        % Update status
        set(handles.pushbutton_reset,'Enable','off');
        set(handles.uipanel_plot,'Visible','on');
        
        % Clear subplot axes
        if isempty(get(handles.uipanel_plot,'Children')) ~= 1
            axis_to_clear = get(handles.uipanel_plot,'Children');
            for num = 1:size(axis_to_clear,1)
                cla(axis_to_clear(num));
            end
        end
        
        
        eval(sprintf('data_%s = [];',device_id_list{i}));           % Create variable based on the last two digit of MAC address
        setappdata(handles.HOT1000_GUI,'start_datetime',datetime);  % Retrieve the recording start time
%         subplot_loc = [2 1];      % Determine the subplot location
        subplot1 = subplot(2,1, ...       % Initiate subplot
            1,'parent',handles.uipanel_plot);
        set(gca, 'TickLength',[0 0]);                               % With no ticks
        box on;                                                     % With grid
        grid on;                                                    % With border
        
        subplot1 = subplot(2,1, ...       % Initiate subplot
            2,'parent',handles.uipanel_plot);
        set(gca, 'TickLength',[0 0]);                               % With no ticks
        box on;                                                     % With grid
        grid on; 
        
        drawnow;
        
    else                                                            % If recording was done
        % Enabling the disabled checkboxes
        set(handles.pushbutton_reset,'Enable','on');
        for check_num = 1:hot1000_device_no
            eval(sprintf('set(handles.checkbox%d,''Enable'',''on'');',check_num));
        end
        
        eval(sprintf('data_%s = getappdata(handles.HOT1000_GUI,''data_%s'');', ...      % Retrieve saved data
            device_id_list{i},device_id_list{i}));
        
        % Update status
        tmp{1} = sprintf('Recording stopped. A total of %.0f samples recorded.', ...
            eval(sprintf('size(data_%s,1)',device_id_list{i})));
        set(handles.text_ins,'String',tmp);
        drawnow;
        
        % Check if .mat file exist
        if eval(sprintf('exist(''%s.mat'') == 0', get(handles.edit_id,'String')))
            eval(sprintf('save(''%s.mat'',''data_%s'');', ...       % Save data as new .mat (based on session ID)
                get(handles.edit_id,'String'),device_id_list{i}));
        else
            eval(sprintf('save(''%s.mat'',''data_%s'',''-append'');', ...       % Save data as existing .mat (based on session ID)
                get(handles.edit_id,'String'),device_id_list{i}));
        end
    end
end

% Status update
xxx = tmp{1};
xxx = sprintf('%s...',xxx(1:30));
% all_subplot_loc = [1 1; 1 2; 1 3; 2 2; 2 3; 2 3];
% subplot_loc = all_subplot_loc(length(checkbox_loc),:);

while get(hObject,'Value')      % If recording button is on/pressed
    
    setappdata(handles.HOT1000_GUI,'record_state',1);   % Status update
    count_plot = 1;                                     % A number to count the number of subplots accordingly to the number of devices
    
    for i = 1:hot1000_device_valid                      % For each device checked
        eval(sprintf('[data_%s(end+1,:),ts] = inlet_%s.pull_sample();', ...         % Acquire data from device
            device_id_list{i},device_id_list{i}));
        
        %Number of the data(how many data is saved at this point)
        num=size(data_97,1);
       
%       Applying filters
        % 1 - Bandpass Filter : 
        % first 2 seconds dont do the bandpass
         [b,a]=butter(4,[0.01/5 0.5/5]); %put it outside
         if num > 24
            BF1=filtfilt(b,a,data_97(1:num,2));
            BF2=filtfilt(b,a,data_97(1:num,3));
         else
            BF1=data_97;
            BF2=data_97;
         end
        % 2 - Moving Average : 
        if num >= 30
%           M(num,1) = sum(BF(num-30+1:num),1)/30;
%           tmp1 = movmean(BF1,30);
%           tmp1 = movmean(BF2,30);
            tmp1 = movmean(BF1(num-30+1:num),30);
            tmp2 = movmean(BF2(num-30+1:num),30);
            MA1(num,1) = tmp1(end);
            MA2(num,1) = tmp2(end);
        else
            tmp1 = movmean(BF1(1:num),length(1:num));
            tmp2 = movmean(BF2(1:num),length(1:num));
            MA1(num,1) = tmp1(end);
            MA2(num,1) = tmp2(end);
        end
        
% debugging
%   size(M)  
        
%         tic
        % Write data1 inside txt file
        fileID1 = fopen('data1.txt', 'w');
        fprintf(fileID1, '%0f\n', data_97(end,2));
        fclose(fileID1);
        
        % Write data2 inside txt file
        fileID2 = fopen('data2.txt', 'w');
        fprintf(fileID2, '%0f\n', data_97(end,3));
        fclose(fileID2);
        
        % Write data3 inside txt file
        fileID3 = fopen('data3.txt', 'w');
        fprintf(fileID3, '%0f\n', data_97(end,4));
        fclose(fileID3);
        
        % Write data1filtered inside txt file
        fileID4 = fopen('data1filtered.txt', 'w');
        fprintf(fileID4, '%0f\n', MA1(end,1));
        fclose(fileID4);
        
        % Write data2filtered inside txt file
        fileID5 = fopen('data2filtered.txt', 'w');
        fprintf(fileID5, '%0f\n', MA2(end,1));
        fclose(fileID5);
%         toc
        
%% Subplot 1 _ RAW DATA
        eval(sprintf('setappdata(handles.HOT1000_GUI,''data_%s'',data_%s);', ...    % Save acquired data as appdata
            device_id_list{i},device_id_list{i}));
        eval(sprintf('subplot%d = subplot(2,1,1,''Parent'',handles.uipanel_plot);',count_plot))  % Create subplot
        cla;                                            % Clear axes
        hold on;
        %Display HbT left change with the red curve
        eval(sprintf('plot(data_%s(:,2),''-r'',''LineWidth'',2);',device_id_list{i}));  % Plot HbT left subtracted
        %Display HbT right change with the green curve
        eval(sprintf('plot(data_%s(:,3),''-g'',''LineWidth'',2);',device_id_list{i}));  % Plot HbT right subtracted
        title(sprintf('Device %d: %s RAW DATA',i,device_id_list{i}));                            % Title the subplot based on device
        set(gca, 'TickLength',[0 0]);                                                   % No tick
        box on;                                                                         % With box
        grid on;                                                                        % With grid
        
        %Animating the graph progress
        if eval(sprintf('size(data_%s,1) > window_size',device_id_list{i}))             % If samples collected > window size
            xlim([eval(sprintf('size(data_%s,1)',device_id_list{i}))+1-window_size ...  % Fixed xlim at [no_samples-window_size+1 no_samples]
                eval(sprintf('size(data_%s,1)',device_id_list{i}))]);
        elseif eval(sprintf('size(data_%s,1) > 1',device_id_list{i}))                   % If samples collected < window size
            xlim([1 100]);                                                              % Fixed xlim at [1 window_size]
        end
        
        %1 data every 0.1 second
        % Updating the number
        if count_plot == hot1000_device_valid
            count_plot = 1;
        else
            count_plot = count_plot + 1;
        end
        drawnow;
        
        %% Subplot 2 _ FILTERED DATA
        eval(sprintf('setappdata(handles.HOT1000_GUI,''data_%s'',data_%s);', ...    % Save acquired data as appdata
            device_id_list{i},device_id_list{i}));
        eval(sprintf('subplot%d = subplot(2,1,2,''Parent'',handles.uipanel_plot);',count_plot))  % Create subplot
        cla;                                            % Clear axes
        hold on;
        %Display HbT left change with the red curve
        plot(MA1(:,1),'-r','LineWidth',2);  % Plot HbT left subtracted
        %Display HbT right change with the green curve
        plot(MA2(:,1),'-g','LineWidth',2);  % Plot HbT right subtracted
        title(sprintf('Device %d: %s FILTERED DATA',i,device_id_list{i}));                            % Title the subplot based on device
        set(gca, 'TickLength',[0 0]);                                                   % No tick
        box on;                                                                         % With box
        grid on;                                                                        % With grid
        
        %Animating the graph progress
        if eval(sprintf('size(data_%s,1) > window_size',device_id_list{i}))             % If samples collected > window size
            xlim([eval(sprintf('size(data_%s,1)',device_id_list{i}))+1-window_size ...  % Fixed xlim at [no_samples-window_size+1 no_samples]
                eval(sprintf('size(data_%s,1)',device_id_list{i}))]);
        elseif eval(sprintf('size(data_%s,1) > 1',device_id_list{i}))                   % If samples collected < window size
            xlim([1 100]);                                                              % Fixed xlim at [1 window_size]
        end
        
        %1 data every 0.1 second
        % Updating the number
        if count_plot == hot1000_device_valid
            count_plot = 1;
        else
            count_plot = count_plot + 1;
        end
        drawnow;
    end
    
    % Update status
    tmp{1} = sprintf('%s %.0f samples recorded.', ...
        xxx, eval(sprintf('size(data_%s,1)',device_id_list{i})));
    set(handles.text_ins,'String',tmp);
    drawnow;
end

% Update status
set(handles.togglebutton_record,'String','Start Recording');
setappdata(handles.HOT1000_GUI,'record_state',0);
drawnow;

% --- Executes during object creation, after setting all properties.
function edit_id_CreateFcn(hObject, eventdata, handles)

if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

% Executes on key press in Session ID.
function edit_id_KeyPressFcn(hObject, eventdata, handles)
drawnow;
maincheckbox_Callback(hObject, eventdata, handles);

function edit_id_Callback(hObject, eventdata, handles)
maincheckbox_Callback(hObject, eventdata, handles);