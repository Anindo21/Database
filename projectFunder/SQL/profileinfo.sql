CREATE VIEW profileinfo as SELECT b.name, b.email, count(spender) as funding, count(ersteller) as created
FROM benutzer b
LEFT JOIN spenden s ON b.email = s.spender
LEFT JOIN projekt p ON p.ersteller = b.email
GROUP BY b.name, b.email;

