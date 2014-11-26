-- Query 1:
-- Need to replace @trajectoryName value with updated trajectory label
use GPSTraj;
set @trajectoryName = "20081023025304";
select count(*) from gps join traj on gps.traj = traj.id where traj.name = @trajectoryName;

-- Query 2:
-- (2a)
-- Query which chooses 10 random dates from the dataset
-- Mongo and Redis depend on the 10 random values that the following query selects
use GPSTraj;
CREATE TABLE random
    select distinct truncate(gps.dateNum, 0) trun from gps
	order by rand()
      limit 10;

-- (2b)
-- Query to retrieve counts for the 10 random dates determined above
use GPSTraj;
select random.trun number_days_since_orig, count(random.trun) gps_pt_Count from gps join random on truncate(gps.dateNum, 0) = random.trun group by random.trun;
