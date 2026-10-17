-- Demo partner network for the prototype: FICTIONAL tailor shops placed at real street-level coordinates
-- in Nuremberg and Erlangen so that distance search and the pilot story ("two cities first") can be shown.
-- Real interview partners live in the team's private notes, not in this public repository.
insert into tailors (id, name, street, postal_code, city, lat, lon, services, certified, turnaround_days, opening_hours, website) values
    ('7c1e0a10-0000-4000-8000-000000000001', 'Änderungsschneiderei am Hauptmarkt', 'Waaggasse 4', '90403', 'Nürnberg', 49.4537, 11.0769, 'HEM_SHORTEN,WAIST_TAKE_IN,SEAM_LET_OUT,SLEEVE_SHORTEN,GENERAL_FITTING', true, 3, 'Mo–Fr 9–18, Sa 10–14', null),
    ('7c1e0a10-0000-4000-8000-000000000002', 'Atelier Lorenz Maßschneiderei', 'Königstraße 32', '90402', 'Nürnberg', 49.4510, 11.0790, 'HEM_SHORTEN,WAIST_TAKE_IN,SEAM_LET_OUT,SLEEVE_SHORTEN,GENERAL_FITTING', true, 5, 'Mo–Fr 10–18', null),
    ('7c1e0a10-0000-4000-8000-000000000003', 'Nadelwerk Gostenhof', 'Gostenhofer Hauptstraße 21', '90443', 'Nürnberg', 49.4498, 11.0605, 'HEM_SHORTEN,WAIST_TAKE_IN,SLEEVE_SHORTEN', true, 2, 'Mo–Sa 9–19', null),
    ('7c1e0a10-0000-4000-8000-000000000004', 'Schneiderei Südstadt', 'Wölckernstraße 18', '90459', 'Nürnberg', 49.4423, 11.0788, 'HEM_SHORTEN,WAIST_TAKE_IN,GENERAL_FITTING', true, 4, 'Mo–Fr 9–17', null),
    ('7c1e0a10-0000-4000-8000-000000000005', 'Änderungsatelier Nordstadt', 'Pirckheimerstraße 12', '90408', 'Nürnberg', 49.4631, 11.0744, 'HEM_SHORTEN,WAIST_TAKE_IN,SEAM_LET_OUT,SLEEVE_SHORTEN', true, 3, 'Di–Fr 10–18, Sa 10–13', null),
    ('7c1e0a10-0000-4000-8000-000000000006', 'Maßatelier Plärrer', 'Gostenhofer Schulgasse 3', '90443', 'Nürnberg', 49.4486, 11.0668, 'SEAM_LET_OUT,SLEEVE_SHORTEN,GENERAL_FITTING', true, 6, 'Mo–Fr 10–18', null),
    ('7c1e0a10-0000-4000-8000-000000000011', 'Änderungsschneiderei Hugenottenplatz', 'Hauptstraße 55', '91054', 'Erlangen', 49.5973, 11.0043, 'HEM_SHORTEN,WAIST_TAKE_IN,SEAM_LET_OUT,SLEEVE_SHORTEN,GENERAL_FITTING', true, 3, 'Mo–Fr 9–18, Sa 9–13', null),
    ('7c1e0a10-0000-4000-8000-000000000012', 'Schneiderstube an den Arcaden', 'Nürnberger Straße 9', '91052', 'Erlangen', 49.5951, 11.0031, 'HEM_SHORTEN,WAIST_TAKE_IN,SLEEVE_SHORTEN', true, 2, 'Mo–Sa 10–20', null),
    ('7c1e0a10-0000-4000-8000-000000000013', 'Maßschneiderei Burgberg', 'Burgbergstraße 40', '91054', 'Erlangen', 49.6035, 11.0085, 'HEM_SHORTEN,WAIST_TAKE_IN,SEAM_LET_OUT,SLEEVE_SHORTEN,GENERAL_FITTING', true, 5, 'Mo–Fr 10–18', null),
    ('7c1e0a10-0000-4000-8000-000000000014', 'Änderungsservice Bruck', 'Fürther Straße 28', '91058', 'Erlangen', 49.5756, 11.0135, 'HEM_SHORTEN,WAIST_TAKE_IN', true, 3, 'Mo–Fr 9–17', null),
    ('7c1e0a10-0000-4000-8000-000000000015', 'Schneiderei Tennenlohe', 'Sebastianstraße 14', '91058', 'Erlangen', 49.5521, 11.0297, 'HEM_SHORTEN,WAIST_TAKE_IN,SLEEVE_SHORTEN,GENERAL_FITTING', true, 4, 'Mo–Fr 9–18', null),
    ('7c1e0a10-0000-4000-8000-000000000021', 'Schneiderei ohne Zertifikat (nicht im Netzwerk)', 'Bahnhofplatz 1', '91054', 'Erlangen', 49.5958, 11.0009, 'HEM_SHORTEN', false, 7, null, null);
