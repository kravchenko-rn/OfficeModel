# OfficeModel
This app creates a 1 month office model.
It creates random number of workers including at least one Director, Booker and Manager. Each worker has his own schedule and work days. Each worker can have more than one position. The Director and the Booker position can be combined only with Manager position. The Cleaner position cannot be combined with any other position.
Every hour the Director generates random number of new tasks and gives them to employees. If there is no employee who can do the job then the new task is given to a freelancer. The task “Clean the office” cannot be given to a freelancer.
Each worker gets payed at the end of the week. The payout depends on the number of hours that a worker spent doing his job. Working on the weekends has double payout.  The Director, the Booker and the Manager positions have fixed salary. Freelancers get paid at the end of the day.
At the end of the month the Booker must create a report about payouts to workers, freelancers. The report must also contain information about executed tasks payout.
