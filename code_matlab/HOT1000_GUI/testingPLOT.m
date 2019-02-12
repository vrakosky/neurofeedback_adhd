
%Test ploting data
load('t1.mat')
plot(data_97(:,2))
plot(data_97(1:1800,2))
[b,a]=butter(4,[0.01/5 0.5/5]);
filtered_data=filtfilt(b,a,data_97(1:1800,2));
figure
hold on
plot(data_97(1:1800,2),'r',filtered_data,'b')
Error using plot
Data must be a single matrix Y or a list of pairs X,Y.
 
plot(data_97(1:1800,2),'r')
plot(filtered_data,'b')