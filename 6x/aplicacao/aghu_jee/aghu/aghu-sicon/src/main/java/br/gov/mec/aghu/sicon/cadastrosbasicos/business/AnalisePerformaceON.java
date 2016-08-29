package br.gov.mec.aghu.sicon.cadastrosbasicos.business;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import br.gov.mec.aghu.core.action.SecurityController;

import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.sicon.dao.AnalisePerformaceDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class AnalisePerformaceON extends BaseBusiness {

	private static final String TEMPO_DA_CONSULTA = "Tempo da consulta: ";
	private static final long serialVersionUID = 2916231442962728811L;
	private static final Log LOG = LogFactory.getLog(AnalisePerformaceON.class);

	@Inject
	private AnalisePerformaceDAO analisePerformaceDAO;
	
	//@Inject
	//private SecurityController securityController;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public List<String> executarCenario1() {
		List<String> listaValores = new ArrayList<String>();
		long tempoInicio = System.currentTimeMillis();
		LOG.info("Teste Performace: Inicio do Processo do cenário 1");

		long tempoInicioConsulta = System.currentTimeMillis();
		List<AinMovimentosInternacao> executarPrimeiraConsulta = this.getAnalisePerformaceDAO().executarPrimeiraConsulta();
		this.calcularLogarTempoExecucao(TEMPO_DA_CONSULTA, tempoInicioConsulta);

		int contador = 0;
		long tempoIteracaoInicio = 0;
		long tempoIteracaoFim = 0;
		for (AinMovimentosInternacao ainMovimentosInternacao : executarPrimeiraConsulta) {
			contador++;
			tempoIteracaoInicio = System.currentTimeMillis();
			AinMovimentosInternacao ret = this.getAnalisePerformaceDAO().executarSegundaConsulta(ainMovimentosInternacao.getSeq());

			LOG.info("Dados: " + ret.getInternacao().getPaciente().getCodigo() + "; " + ret.getInternacao().getDthrInternacao() + "; "
					+ ret.getEspecialidade().getNomeReduzido());
			tempoIteracaoFim = System.currentTimeMillis();
			listaValores.add(contador + ";" + tempoIteracaoInicio + ";" + tempoIteracaoFim);
		}

		LOG.info("Teste Performace: Fim do Processo do cenário 1");
		this.calcularLogarTempoExecucao("Tempo TOTAL do processo  do cenário 1 = ", tempoInicio);
		return listaValores;
	}

	public List<String> executarCenario11() {
		List<String> listaValores = new ArrayList<String>();
		long tempoInicio = System.currentTimeMillis();
		LOG.info("Teste Performace: Inicio do Processo do cenário 1.1");

		long tempoInicioConsulta = System.currentTimeMillis();
		List<AinMovimentosInternacao> executarPrimeiraConsulta = this.getAnalisePerformaceDAO().executarPrimeiraConsulta();
		this.calcularLogarTempoExecucao(TEMPO_DA_CONSULTA, tempoInicioConsulta);

		int contador = 0;
		long tempoIteracaoInicio = 0;
		long tempoIteracaoFim = 0;
		for (AinMovimentosInternacao ainMovimentosInternacao : executarPrimeiraConsulta) {
			contador++;
			tempoIteracaoInicio = System.currentTimeMillis();

			AinMovimentosInternacao ret = this.getAnalisePerformaceDAO().executarSegundaConsulta(ainMovimentosInternacao.getSeq());

			LOG.info("Dados: " + ret.getIntSeq());

			tempoIteracaoFim = System.currentTimeMillis();
			listaValores.add(contador + ";" + tempoIteracaoInicio + ";" + tempoIteracaoFim);
		}

		LOG.info("Teste Performace: Fim do Processo do cenário 1.1");
		this.calcularLogarTempoExecucao("Tempo TOTAL do processo do cenário 1.1 = ", tempoInicio);
		return listaValores;
	}

	public List<String> executarCenario12() {
		List<String> listaValores = new ArrayList<String>();
		long tempoInicio = System.currentTimeMillis();
		LOG.info("Teste Performace: Inicio do Processo do cenário 1.2");

		long tempoInicioConsulta = System.currentTimeMillis();
		List<AinMovimentosInternacao> executarPrimeiraConsulta = this.getAnalisePerformaceDAO().executarPrimeiraConsulta();
		this.calcularLogarTempoExecucao(TEMPO_DA_CONSULTA, tempoInicioConsulta);

		int contador = 0;
		long tempoIteracaoInicio = 0;
		long tempoIteracaoFim = 0;
		for (AinMovimentosInternacao ainMovimentosInternacao : executarPrimeiraConsulta) {
			contador++;
			tempoIteracaoInicio = System.currentTimeMillis();

			this.getAnalisePerformaceDAO().executarSegundaConsulta(ainMovimentosInternacao.getSeq());

			tempoIteracaoFim = System.currentTimeMillis();
			listaValores.add(contador + ";" + tempoIteracaoInicio + ";" + tempoIteracaoFim);
		}

		LOG.info("Teste Performace: Fim do Processo do cenário 1.2");
		this.calcularLogarTempoExecucao("Tempo TOTAL do processo do cenário 1.2 = ", tempoInicio);
		return listaValores;
	}

	public List<String> executarCenario2() {
		List<String> listaValores = new ArrayList<String>();
		long tempoInicio = System.currentTimeMillis();
		LOG.info("Teste Performace: Inicio do Processo do cenário 2");

		long tempoInicioConsulta = System.currentTimeMillis();
		List<AinMovimentosInternacao> executarPrimeiraConsulta = this.getAnalisePerformaceDAO().executarPrimeiraConsulta();
		this.calcularLogarTempoExecucao(TEMPO_DA_CONSULTA, tempoInicioConsulta);

		int contador = 0;
		long tempoIteracaoInicio = 0;
		long tempoIteracaoFim = 0;
		for (AinMovimentosInternacao ainMovimentosInternacao : executarPrimeiraConsulta) {
			contador++;
			tempoIteracaoInicio = System.currentTimeMillis();

			//securityController.usuarioTemPermissao("liberaConsulta", "liberar");
			//this.getIdentity().hasPermission("liberaConsulta", "liberar");

			AinMovimentosInternacao ret = this.getAnalisePerformaceDAO().executarSegundaConsulta(ainMovimentosInternacao.getSeq());

			LOG.info("Dados: " + ret.getInternacao().getPaciente().getCodigo() + "; " + ret.getInternacao().getDthrInternacao() + "; "
					+ ret.getEspecialidade().getNomeReduzido());

			tempoIteracaoFim = System.currentTimeMillis();
			listaValores.add(contador + ";" + tempoIteracaoInicio + ";" + tempoIteracaoFim);
		}

		LOG.info("Teste Performace: Fim do Processo do cenário 2");
		this.calcularLogarTempoExecucao("Tempo TOTAL do processo  do cenário 2 = ", tempoInicio);
		return listaValores;
	}

	public List<String> executarCenario3() {
		List<String> listaValores = new ArrayList<String>();
		long tempoInicio = System.currentTimeMillis();
		LOG.info("Teste Performace: Inicio do Processo do cenário 3");

		long tempoIteracaoInicio = 0;
		long tempoIteracaoFim = 0;

		tempoIteracaoInicio = System.currentTimeMillis();

		this.getAnalisePerformaceDAO().executarProcedimentoC3();

		tempoIteracaoFim = System.currentTimeMillis();
		listaValores.add(1 + ";" + tempoIteracaoInicio + ";" + tempoIteracaoFim);

		LOG.info("Teste Performace: Fim do Processo do cenário 3");
		this.calcularLogarTempoExecucao("Tempo TOTAL do processo  do cenário 3 = ", tempoInicio);
		return listaValores;
	}

	public List<String> executarCenario31() {
		List<String> listaValores = new ArrayList<String>();
		long tempoInicio = System.currentTimeMillis();
		LOG.info("Teste Performace: Inicio do Processo do cenário 3.1");

		int contador = 0;
		long tempoIteracaoInicio = 0;
		long tempoIteracaoFim = 0;
		for (int i = 0; i < 1000; i++) {
			contador++;
			tempoIteracaoInicio = System.currentTimeMillis();

			this.getAnalisePerformaceDAO().executarProcedimentoC3();

			tempoIteracaoFim = System.currentTimeMillis();
			listaValores.add(contador + ";" + tempoIteracaoInicio + ";" + tempoIteracaoFim);
		}

		LOG.info("Teste Performace: Fim do Processo do cenário 3.1");
		this.calcularLogarTempoExecucao("Tempo TOTAL do processo  do cenário 3.1 = ", tempoInicio);
		return listaValores;
	}

	public void calcularLogarTempoExecucao(String texto, long tempoInicio) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		LOG.info("Teste Performace: " + texto + df.format(new Date(System.currentTimeMillis() - tempoInicio)));
	}

	private AnalisePerformaceDAO getAnalisePerformaceDAO() {
		return this.analisePerformaceDAO;
	}
}
