package br.gov.mec.aghu.exames;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.exames.dao.VAelSolicAtendsDAO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class VAelSolicAtendsDAOTest  extends AbstractDAOTest<VAelSolicAtendsDAO> {
	
	@Override
	protected VAelSolicAtendsDAO doDaoUnderTests() {
		return new VAelSolicAtendsDAO() {
			private static final long serialVersionUID = 746877847243538137L;
			
			@Override
			protected org.hibernate.SQLQuery createSQLQuery(String query) {
				return VAelSolicAtendsDAOTest.this.createSQLQuery(query);
			};
			
		};
	}

	@Override
	protected void initMocks() {

	}
	
	@Override
	protected void finalizeMocks() {
		
	}
	
	@Test
	public void pesquisarVPLAgregado() {
		if (isEntityManagerOk()) {
			AghUnidadesFuncionais aghUnidadesFuncionais = new AghUnidadesFuncionais();
			aghUnidadesFuncionais.setSeq(new Short("100"));
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 17);
			cal.set(Calendar.MONTH, 6);
			cal.set(Calendar.YEAR, 2016);
			cal.set(Calendar.HOUR, 20);
			cal.set(Calendar.MINUTE, 30);
			cal.set(Calendar.SECOND, 00);
			
			Date inicio = new Date();
			
			DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			
			try {
				getDaoUnderTests().pesquisarVPLAgregado(aghUnidadesFuncionais, cal.getTime(), "AC");
			} catch (BaseException e) {
			  Assert.fail(e.getMessage());
			}
			Date fim = new Date();
			System.out.println("Início: " + formatter.format(inicio));
			System.out.println("Fim: " + formatter.format(fim));
			System.out.println("Tempo de consulta: " + DateUtil.calculaDiferencaTempo(inicio, fim));
			
			Assert.assertTrue(true);
		}
	}
}