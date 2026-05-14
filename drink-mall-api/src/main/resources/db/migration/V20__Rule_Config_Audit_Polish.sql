INSERT IGNORE INTO sys_config (config_key, config_value, description) VALUES
('level.normal.name', '普通会员', '会员等级名称'),
('level.promoter.name', '推客', '会员等级名称'),
('level.county.name', '县级联营商', '会员等级名称'),
('level.city.name', '市级联营商', '会员等级名称'),
('level.province.name', '省级联营商', '会员等级名称'),
('level.normal.order', '0', '会员等级顺序'),
('level.promoter.order', '1', '会员等级顺序'),
('level.county.order', '2', '会员等级顺序'),
('level.city.order', '3', '会员等级顺序'),
('level.province.order', '4', '会员等级顺序'),
('level.county.benefits', '直推首购 20% 佣金|招商奖励按配置比例发放|扶商奖励按配置比例发放', '县级联营商权益摘要，可配置'),
('level.city.benefits', '直推首购 20% 佣金|招商奖励按配置比例发放|扶商奖励按配置比例发放', '市级联营商权益摘要，可配置'),
('level.province.benefits', '直推首购 20% 佣金|招商奖励按配置比例发放|扶商奖励按配置比例发放', '省级联营商权益摘要，可配置');
