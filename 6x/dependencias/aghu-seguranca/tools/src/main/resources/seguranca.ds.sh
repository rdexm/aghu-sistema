VERSAO_SEGURANCA=2.1-SNAPSHOT
DATABASE_DATASOURCE=java:/aghuDatasource
JBOSS_HOME=/opt/aghu/jboss
LOCAL_IP=`ip addr show dev eth0 | egrep -o '([[:digit:]]{1,3}\.){3}[[:digit:]]{1,3}' | head -n1`
JNDI_PORT=1099

case $1 in

importar-seguranca)
   echo "Iniciando Importação de Segurança..."
   java -cp casca-tools-$VERSAO_SEGURANCA.jar:$JBOSS_HOME/client/*:lib/* -Djava.naming.factory.initial=org.jnp.interfaces.NamingContextFactory -Djava.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces -Djava.naming.provider.url=jnp://$LOCAL_IP:$JNDI_PORT -Dhibernate.connection.datasource=$DATABASE_DATASOURCE -Dfile.encoding=UTF-8 br.gov.mec.casca.tools.security.xml.DatabaseUpdater ./mapeamento false nda aghu 5 conf/hibernate.ds.sh.cfg.xml
;;
exportar-seguranca)
   echo "Iniciando Exportação de Segurança..."
   read -n 1 -p 'Todas as configurações do arquivo XML serão substituídas pelas existentes no banco de dados. Deseja prosseguir? (S/N)?'
   java -cp casca-tools-$VERSAO_SEGURANCA.jar:$JBOSS_HOME/client/*:lib/* -Djava.naming.factory.initial=org.jnp.interfaces.NamingContextFactory -Djava.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces -Djava.naming.provider.url=jnp://$LOCAL_IP:$JNDI_PORT -Dhibernate.connection.datasource=$DATABASE_DATASOURCE -Dfile.encoding=UTF-8 br.gov.mec.casca.tools.security.xml.XMLWriter ./mapeamento false nda aghu 5 conf/hibernate.ds.sh.cfg.xml
;;
importar-menu)
   echo "Iniciando Importação de Menu..."
   java -cp casca-tools-$VERSAO_SEGURANCA.jar:$JBOSS_HOME/client/*:lib/* -Djava.naming.factory.initial=org.jnp.interfaces.NamingContextFactory -Djava.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces -Djava.naming.provider.url=jnp://$LOCAL_IP:$JNDI_PORT -Dhibernate.connection.datasource=$DATABASE_DATASOURCE -Dfile.encoding=UTF-8 br.gov.mec.casca.tools.menu.xml.MenuUpdater ./menu false nda aghu 5 conf/hibernate.ds.sh.cfg.xml
;;
exportar-menu)
   echo "Iniciando Exportação de Menu..."
   read -n 1 -p 'Todas as configurações do arquivo XML serão substituídas pelas existentes no banco de dados. Deseja prosseguir? (S/N)?'
   java -cp casca-tools-$VERSAO_SEGURANCA.jar:$JBOSS_HOME/client/*:lib/* -Djava.naming.factory.initial=org.jnp.interfaces.NamingContextFactory -Djava.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces -Djava.naming.provider.url=jnp://$LOCAL_IP:$JNDI_PORT -Dhibernate.connection.datasource=$DATABASE_DATASOURCE -Dfile.encoding=UTF-8 br.gov.mec.casca.tools.menu.xml.XMLWriter ./menu false nda aghu 5 conf/hibernate.ds.sh.cfg.xml
;;
*)
   echo -p "Esse script deve ser executado da seguinte forma:\n\n/opt/aghu/aghu-seguranca-<versao>/seguranca.sh {importar-seguranca|exportar-seguranca|importar-menu|exportar-menu}\n"
;;

esac
exit 0
