DATABASE_URL=jdbc:postgresql://<BD_HOST>:<BD_PORTA>/<BD_BASE>
DATABASE_USERNAME=<BD_USUARIO>
DATABASE_PASSWORD=<BD_SENHA>

case $1 in

importar-seguranca)
   echo "Iniciando Importação de Segurança..."
   java -cp casca-tools-@version@.jar:lib/* -Dhibernate.connection.url=$DATABASE_URL -Dhibernate.connection.username=$DATABASE_USERNAME -Dhibernate.connection.password=$DATABASE_PASSWORD -Dfile.encoding=UTF-8 br.gov.mec.casca.tools.security.xml.DatabaseUpdater ./mapeamento false nda conf/hibernate.sh.cfg.xml
;;
exportar-seguranca)
   echo "Iniciando Exportação de Segurança..."
   read -n 1 -p 'Todas as configurações do arquivo XML serão substituídas pelas existentes no banco de dados. Deseja prosseguir? (S/N)?'
   java -cp casca-tools-@version@.jar:lib/* -Dhibernate.connection.url=$DATABASE_URL -Dhibernate.connection.username=$DATABASE_USERNAME -Dhibernate.connection.password=$DATABASE_PASSWORD -Dfile.encoding=UTF-8 br.gov.mec.casca.tools.security.xml.XMLWriter ./mapeamento false nda conf/hibernate.sh.cfg.xml
;;
importar-menu)
   echo "Iniciando Importação de Menu..."
   java -cp casca-tools-@version@.jar:lib/* -Dhibernate.connection.url=$DATABASE_URL -Dhibernate.connection.username=$DATABASE_USERNAME -Dhibernate.connection.password=$DATABASE_PASSWORD -Dfile.encoding=UTF-8 br.gov.mec.casca.tools.menu.xml.MenuUpdater ./menu false nda conf/hibernate.sh.cfg.xml
;;
exportar-menu)
   echo "Iniciando Exportação de Menu..."
   read -n 1 -p 'Todas as configurações do arquivo XML serão substituídas pelas existentes no banco de dados. Deseja prosseguir? (S/N)?'
   java -cp casca-tools-@version@.jar:lib/* -Dhibernate.connection.url=$DATABASE_URL -Dhibernate.connection.username=$DATABASE_USERNAME -Dhibernate.connection.password=$DATABASE_PASSWORD -Dfile.encoding=UTF-8 br.gov.mec.casca.tools.menu.xml.XMLWriter ./menu false nda conf/hibernate.sh.cfg.xml
;;
*)
   echo -p "Esse script deve ser executado da seguinte forma:\n\n/opt/aghu/aghu-seguranca-<versao>/seguranca.sh {importar-seguranca|exportar-seguranca|importar-menu|exportar-menu}\n"
;;

esac
exit 0
