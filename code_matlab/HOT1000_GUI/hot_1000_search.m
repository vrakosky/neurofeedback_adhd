function [hot1000_device_no hot1000_lsl_streams] = hot_1000_search(lib,handles)
hot_1000_valid = 0;
hot1000_device_no = 0;
hot1000_lsl_streams = {};
list_hot1000_id = {};

while hot_1000_valid == 0
    lsl_streams = {};
    while isempty(hot1000_lsl_streams)
        lsl_streams = lsl_resolve_all(lib);   % Read from all devices
        %           lsl_streams = lsl_resolve_byprop(lib,'source_id','78:61:7c:34:25:fd');  % Read from specific device
        
        for device_no = 1:size(lsl_streams,2)
            tmp = lsl_streams{device_no}.source_id;
            if length(tmp) == 17 && strcmp(tmp(1:15),'78:61:7c:34:25:') == 1 ...
                    && ismember(tmp,list_hot1000_id) ~= 1
                hot1000_device_no = hot1000_device_no + 1;
                hot1000_lsl_streams{hot1000_device_no} = lsl_streams{device_no};
                list_hot1000_id{end+1} = tmp;
                hot_1000_valid = 1;
            end
        end
        
    end
end

set(handles.text_ins,'String',{'Scanning done. You can either scan again or start recording.' ...
    sprintf('Number of HOT1000 device(s) found: %d (0 Selected)', ...
    hot1000_device_no) ...
    sprintf('      No.         MAC Address')});
clear tmp;

for i = 1:hot1000_device_no
    
    tmp = get(handles.text_ins,'String');
    tmp(end+1,:) = {sprintf('      %d)        %s', ...
        i, hot1000_lsl_streams{i}.source_id)};
    set(handles.text_ins,'String',tmp);
end
%       source_id = lsl_streams{r}.source_id;
%       disp('x2');
%       if source_id(1:8) == '78:61:7c'
%         display_string = ['Found HOT-1000, source_id is ' source_id];
%         disp(display_string);
%         hot_1000_lsl_id{i} = lsl_streams{r}.source_id;
% %         i=i+1;
%         inlet = lsl_inlet(lsl_streams{r});
%         hot_1000_valid == 1;
%       end
%     end
%     disp('x3');
%   end
%   i_string = num2string(i-1);
%   string = ['Found ' i_string ' HOT-1000'];
%   disp(string);
%   disp('x4');
