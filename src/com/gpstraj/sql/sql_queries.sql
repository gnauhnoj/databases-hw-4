use GPSTraj;
set @trajectoryName = "20081023025304";
select count(*) from gps join traj on gps.traj = traj.id where traj.name = @trajectoryName;

create view random as
   select distinct truncate(gps.dateNum, 0) trun from gps
	order by rand()
     limit 10;

select random.trun number_days_since_orig, count(random.trun) gps_pt_Count from gps join random on truncate(gps.dateNum, 0) = random.trun group by random.trun;
