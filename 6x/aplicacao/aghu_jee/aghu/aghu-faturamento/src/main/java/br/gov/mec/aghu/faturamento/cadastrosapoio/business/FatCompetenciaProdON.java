package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoCompProd;
import br.gov.mec.aghu.dominio.DominioSituacaoProducao;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaProdDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoContaProdDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoItemContaHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoItemContaProdDAO;
import br.gov.mec.aghu.model.FatCompetenciaProd;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatEspelhoContaProd;
import br.gov.mec.aghu.model.FatEspelhoItemContaHosp;
import br.gov.mec.aghu.model.FatEspelhoItemContaProd;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatCompetenciaProdON extends BaseBusiness {

	private static final String CONTA = "Conta ";

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final Log LOG = LogFactory.getLog(FatCompetenciaProdON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;
	
	@Inject
	private FatCompetenciaProdDAO fatCompetenciaProdDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private FatEspelhoItemContaProdDAO fatEspelhoItemContaProdDAO;
	
	@Inject
	private FatEspelhoContaProdDAO fatEspelhoContaProdDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private FatEspelhoAihDAO fatEspelhoAihDAO;
	
	@Inject
	private FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO;

	private static final long serialVersionUID = -6084223513070995999L;

	public enum FatCompetenciaProdONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_DATA_FORA_PERIODO_COMPETENCIA_PRODUCAO;
	}

	public void gravarFatCompetenciaProd(FatCompetenciaProd competenciaProd, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.valida(competenciaProd);

		competenciaProd.setAlteradoEm(new Date());
		if (servidorLogado != null) {
			competenciaProd.setAlteradoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);
		} else {
			competenciaProd.setAlteradoPor(null);
		}

		this.getFatCompetenciaProdDAO().atualizar(competenciaProd);
//		this.getFatCompetenciaProdDAO().flush();
	}

	private void valida(FatCompetenciaProd competenciaProducao)
			throws ApplicationBusinessException {

		if (competenciaProducao != null) {
			Calendar dataMesCompetencia = Calendar.getInstance();

			dataMesCompetencia.set(Calendar.MONTH, competenciaProducao.getMes()
					.intValue());

			if ((competenciaProducao.getDthrFimProd().compareTo(
					competenciaProducao.getDthrInicioProd()) < 0)
					&& (competenciaProducao.getDthrFimProd().compareTo(
							this.ultimoDiaMesCompetencia(dataMesCompetencia
									.getTime())) <= 0)) {

				throw new ApplicationBusinessException(
						FatCompetenciaProdONExceptionCode.MENSAGEM_DATA_FORA_PERIODO_COMPETENCIA_PRODUCAO);
			}
		}
	}

	// @SuppressWarnings("PMD.ExcessiveMethodLength")
	public void gravarProducao(final Date dataFimVinculoServidor) throws BaseException {

		FatEspelhoContaProd espelhoContaProd;

		// Localiza contas a serem contabilizadas como produção do mês
		List<FatContasHospitalares> listaContasHospitalares = this
				.getFatContasHospitalaresDAO().listarContasHospitalares();

		for (FatContasHospitalares fatContasHospitalares : listaContasHospitalares) {

			FatEspelhoAih espelhoAih = this
					.obtemFatEspelhoAih(fatContasHospitalares);

			if (espelhoAih != null) {

				espelhoContaProd = this
						.getFatEspelhoContaProdDAO()
						.obterPorContaHospitalar(fatContasHospitalares);
				
				if (espelhoContaProd == null && (fatContasHospitalares.getIndContaReapresentada() != null && fatContasHospitalares.getIndContaReapresentada())){
					FatContasHospitalares contaMae = fatContasHospitalares.getContaHospitalarReapresentada();
 					
					if (contaMae != null){
						espelhoContaProd = this.getFatEspelhoContaProdDAO().obterPorContaHospitalar(contaMae);
					}
				}				

				// conta não existe na FatEspelhoContaProd 
				if (espelhoContaProd == null) {

					gravaContaInexistenteNaoReapresentada(
							fatContasHospitalares, espelhoAih, dataFimVinculoServidor);

					// é conta reapresentada
				} else if ((fatContasHospitalares.getIndContaReapresentada() != null)
						&& (fatContasHospitalares.getIndContaReapresentada())) {

					gravaContaReapresentada(fatContasHospitalares, espelhoAih, dataFimVinculoServidor);

					// conta já existe
				} else if (espelhoContaProd != null) {

					gravaContaJaExistente(fatContasHospitalares, espelhoAih,espelhoContaProd, dataFimVinculoServidor);

				}
			}
		}
	}

	public void gravaContaInexistenteNaoReapresentada(
			FatContasHospitalares fatContasHospitalares,
			FatEspelhoAih espelhoAih, final Date dataFimVinculoServidor) throws BaseException {
		BigDecimal valorTotalProducao = BigDecimal.ZERO;

		if (fatContasHospitalares != null) {
			valorTotalProducao = this.calculaValorTotal(fatContasHospitalares);
		}

		FatEspelhoContaProd espelhoContaProdCopia = this
				.copiaEspelhoAihParaEspelhoContaProd(espelhoAih,
						valorTotalProducao, DominioSituacaoProducao.P, dataFimVinculoServidor);

		if (espelhoContaProdCopia != null) {
			this.gravaEspelhoConta(espelhoContaProdCopia);
		}

		List<FatEspelhoItemContaHosp> espelhoItemContaHospLista = this
				.getFatEspelhoItemContaHospDAO()
				.listarEspelhosItensContaHospitalar(
						fatContasHospitalares.getSeq());

		if (espelhoItemContaHospLista.size() > 0) {
			this.gravaEspelhoItensConta(espelhoItemContaHospLista,
					espelhoContaProdCopia);
		}
	}

	public void gravaContaReapresentada(
			FatContasHospitalares fatContasHospitalares,
			FatEspelhoAih espelhoAih, final Date dataFimVinculoServidor) throws BaseException {

		BigDecimal valorTotalConta = this
				.calculaValorTotal(fatContasHospitalares);

		BigDecimal valorTotal = BigDecimal.ZERO;

		// busca mãe	
		FatContasHospitalares contaHospitalar = fatContasHospitalares.getContaHospitalarReapresentada();		

		while (contaHospitalar != null) {

			FatEspelhoContaProd espelhoContaProd = this.getFatEspelhoContaProdDAO().obterContaHospitalar(contaHospitalar);

			if (espelhoContaProd != null  &&  espelhoContaProd.getValorTotalProd() != null) {
				valorTotal = valorTotal.add(espelhoContaProd.getValorTotalProd());
			}

			contaHospitalar = contaHospitalar.getContaHospitalarReapresentada();
		}

		if (!valorTotalConta.equals(valorTotal) && !valorTotal.equals(BigDecimal.ZERO)) {

			BigDecimal valorTotalProducao = valorTotalConta.subtract(valorTotal);

			FatEspelhoContaProd espelhoContaProdCopia = this
					.copiaEspelhoAihParaEspelhoContaProd(espelhoAih,
							valorTotalProducao, DominioSituacaoProducao.D, dataFimVinculoServidor);

			if (espelhoContaProdCopia != null) {
				this.gravaEspelhoConta(espelhoContaProdCopia);
			}

			List<FatEspelhoItemContaHosp> espelhoItemContaHospLista = this
					.getFatEspelhoItemContaHospDAO()
					.listarEspelhosItensContaHospitalar(
							fatContasHospitalares.getSeq());

			this.gravaEspelhoItensConta(espelhoItemContaHospLista,
					espelhoContaProdCopia);
		}
	}

	public void gravaContaJaExistente(
			FatContasHospitalares fatContasHospitalares,
			FatEspelhoAih espelhoAih, FatEspelhoContaProd espelhoContaProd, final Date dataFimVinculoServidor)
			throws BaseException {
		BigDecimal valorTotalConta = this
				.calculaValorTotal(fatContasHospitalares);

		BigDecimal valorTotalProducao = espelhoContaProd.getValorTotalProd();

		if (!valorTotalProducao.equals(valorTotalConta)) {

			if (fatContasHospitalares != null) {
				valorTotalProducao = valorTotalProducao.subtract(valorTotalConta);
			}

			FatEspelhoContaProd espelhoContaProdCopia = this
					.copiaEspelhoAihParaEspelhoContaProd(espelhoAih,
							valorTotalProducao, DominioSituacaoProducao.D, dataFimVinculoServidor);

			if (espelhoContaProdCopia != null) {
				this.gravaEspelhoConta(espelhoContaProdCopia);
			}

			List<FatEspelhoItemContaHosp> espelhoItemContaHospLista = this
					.getFatEspelhoItemContaHospDAO()
					.listarEspelhosItensContaHospitalar(
							fatContasHospitalares.getSeq());

			this.gravaEspelhoItensConta(espelhoItemContaHospLista,
					espelhoContaProdCopia);
		}
	}

	public void iniciarNovaCompetencia(final Date dataFimVinculoServidor) throws ApplicationBusinessException{
		
		// Encerra competência ativa
		Date dataFimUltimaCompetencia =  fecharCompetencia(dataFimVinculoServidor);

		// Inicia nova competência
		abrirNovaCompetencia(dataFimUltimaCompetencia, dataFimVinculoServidor);
	}
	
	public Date fecharCompetencia(final Date dataFimVinculoServidor) {
		FatCompetenciaProd competenciaProd = this.getFatCompetenciaProdDAO()
				.obtemCompetenciaAtiva();

		if (competenciaProd == null){
			return null;
		}else{	
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			try {
				competenciaProd.setIndSituacao(DominioSituacaoCompProd.F);
				competenciaProd.setAlteradoEm(new Date());
				competenciaProd.setAlteradoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);
	
				this.getFatCompetenciaProdDAO().atualizar(competenciaProd);
//				this.getFatCompetenciaProdDAO().flush();
	
			} catch (Exception e) {
				logError(EXCECAO_CAPTURADA, e);
			}
	
			return competenciaProd.getDthrFimProd();
		}
	}

	public void abrirNovaCompetencia(Date dataFimUltimaCompetencia, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		

		FatCompetenciaProd competenciaProd = new FatCompetenciaProd();
		
		competenciaProd.setCriadoEm(new Date());
		competenciaProd.setCriadoPor(servidorLogado
				.getUsuario());

		competenciaProd.setAlteradoEm(new Date());
		competenciaProd.setAlteradoPor(servidorLogado
				.getUsuario());
		
		if (dataFimUltimaCompetencia == null){
			dataFimUltimaCompetencia = new Date();
		}

		competenciaProd.setDthrInicioProd(dataFimUltimaCompetencia);
		
		Calendar dataFinalNovaCompetencia = Calendar.getInstance();
		dataFinalNovaCompetencia.setTime(dataFimUltimaCompetencia);
		dataFinalNovaCompetencia.add(Calendar.MONTH, 1);	
		// O padrão de Período de Competência determina que um período encerre no dia 20
		dataFinalNovaCompetencia.set(dataFinalNovaCompetencia.get(Calendar.YEAR), dataFinalNovaCompetencia.get(Calendar.MONTH), 20);
		competenciaProd.setDthrFimProd(dataFinalNovaCompetencia.getTime());
		
		Integer mes = dataFinalNovaCompetencia.get(Calendar.MONTH) + 1;
		competenciaProd.setMes(mes.byteValue());
		
		Integer ano = dataFinalNovaCompetencia.get(Calendar.YEAR);
		competenciaProd.setAno(ano.shortValue());

		competenciaProd.setIndSituacao(DominioSituacaoCompProd.A);

		try {
			this.getFatCompetenciaProdDAO().persistir(competenciaProd);
//			this.getFatCompetenciaProdDAO().flush();
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
		}
	}

	public boolean atualizarCompetenciaProducao(final Date dataFimVinculoServidor) {
		boolean result = false;

		FatCompetenciaProd competenciaAberta = this.getFatCompetenciaProdDAO()
				.obtemCompetenciaAtiva();

		List<FatCompetenciaProd> competenciaAnteriorList = this
				.getFatCompetenciaProdDAO().obtemCompetenciaAnterior();

		FatContasHospitalares contaHospitalar = this
				.getFatContasHospitalaresDAO()
				.obterContaHospitalarPorCompetenciaProd(
						competenciaAnteriorList.get(0));

		if (contaHospitalar != null) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			try {
				contaHospitalar.setCompetenciaProd(competenciaAberta);
				contaHospitalar.setAlteradoEm(new Date());
				contaHospitalar.setAlteradoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);

				this.getFatContasHospitalaresDAO().atualizar(contaHospitalar);
//				this.getFatContasHospitalaresDAO().flush();

				result = true;
			} catch (Exception e) {
				logError(EXCECAO_CAPTURADA, e);
				result = false;
			}
		}
		return result;
	}

	private FatEspelhoAih obtemFatEspelhoAih(
			FatContasHospitalares fatContasHospitalares) {

		FatEspelhoAih espelhoAih = this.getFatEspelhoAihDAO()
				.obterPorCthSeqSeqp(fatContasHospitalares.getSeq(),
						Integer.valueOf(1));
		return espelhoAih;

	}

	private void gravaEspelhoItensConta(
			List<FatEspelhoItemContaHosp> espelhoItemContaHospLista,
			FatEspelhoContaProd espelhoContaProd) throws BaseException {

		final List<String> seqsOK = new ArrayList<String>();
		final List<String> seqsNOK = new ArrayList<String>();

		for (FatEspelhoItemContaHosp fatEspelhoItemContaHosp : espelhoItemContaHospLista) {

			FatEspelhoItemContaProd espelhoItemConta = this
					.copiaEspelhoItensContaParaEspelhoItensContaProd(
							fatEspelhoItemContaHosp, espelhoContaProd);

			if (this.insereEspelhoItensConta(espelhoItemConta)) {
				seqsOK.add(CONTA + espelhoItemConta.getSeq()
						+ " encerrada com sucesso.<br />");
				logInfo(CONTA + espelhoItemConta.getSeq()
						+ " encerrada com sucesso.");
			} else {
				seqsNOK.add(CONTA + espelhoItemConta.getIchCthSeq()
						+ " com erros no item " + espelhoItemConta.getIchSeq() + "<br />");
				logError(CONTA + espelhoItemConta.getIchCthSeq()
						+ " com erros no item " + espelhoItemConta.getIchSeq() + "<br />");
			}
		}

		// getFaturamentoFacade().enviaEmailResultadoEncerramentoCTHs(seqsOK,
		// seqsNOK);

	}

	private boolean insereEspelhoItensConta(
			FatEspelhoItemContaProd espelhoItemConta) {

		boolean result = true;

		try {
			this.getFatEspelhoItemContaProdDAO().persistir(espelhoItemConta);
//			this.getFatEspelhoItemContaProdDAO().flush();

			result = true;
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			result = false;
		}

		return result;
	}

	private void gravaEspelhoConta(FatEspelhoContaProd espelhoContaProd)
			throws BaseException {
		final List<String> seqsOK = new ArrayList<String>();
		final List<String> seqsNOK = new ArrayList<String>();

		try {
			if (this.insereEspelhoConta(espelhoContaProd)) {
				seqsOK.add(CONTA + espelhoContaProd.getContaHospitalar().getCthSeq()
						+ " inserida com sucesso.<br />");
				logInfo(CONTA + espelhoContaProd.getContaHospitalar().getCthSeq()
						+ " inserida com sucesso.");
			} else {
				seqsNOK.add(CONTA + espelhoContaProd.getContaHospitalar().getCthSeq()
						+ " não inserida por conter erros.<br />");
				logError(CONTA + espelhoContaProd.getContaHospitalar().getCthSeq()
						+ " não inserida por conter erros.");
			}
		} catch (BaseException e) {
			seqsNOK.add(CONTA + espelhoContaProd.getContaHospitalar().getCthSeq()
					+ " não inserida por conter erros.<br /> " + e.getMessage());
			logError(CONTA + espelhoContaProd.getContaHospitalar().getCthSeq()
					+ " não inserida por conter erros.");
		}

		// this.getFaturamentoFacade().enviaEmailResultadoEncerramentoCTHs(seqsOK,
		// seqsNOK);

	}

	private boolean insereEspelhoConta(FatEspelhoContaProd espelhoContaProd)
			throws BaseException {

		boolean result = false;

		try {
			this.getFatEspelhoContaProdDAO().persistir(espelhoContaProd);
//			this.getFatEspelhoContaProdDAO().flush();

			result = true;
		} catch (Exception e) {
			logError(EXCECAO_CAPTURADA, e);
			result = false;
		}

		return result;
	}
	@SuppressWarnings("PMD.NPathComplexity")
	private BigDecimal calculaValorTotal(FatContasHospitalares contaHospitalar) {
		BigDecimal valorTotalProducao = BigDecimal.ZERO;

		if (contaHospitalar == null) {
			return valorTotalProducao;
		}

		if (contaHospitalar.getValorSh() != null) {
			valorTotalProducao = valorTotalProducao.add(contaHospitalar
					.getValorSh());
		}

		if (contaHospitalar.getValorUti() != null) {
			valorTotalProducao = valorTotalProducao.add(contaHospitalar
					.getValorUti());
		}

		if (contaHospitalar.getValorUtie() != null) {
			valorTotalProducao = valorTotalProducao.add(contaHospitalar
					.getValorUtie());
		}

		if (contaHospitalar.getValorSp() != null) {
			valorTotalProducao = valorTotalProducao.add(contaHospitalar
					.getValorSp());
		}

		if (contaHospitalar.getValorAcomp() != null) {
			valorTotalProducao = valorTotalProducao.add(contaHospitalar
					.getValorAcomp());
		}

		if (contaHospitalar.getValorRn() != null) {
			valorTotalProducao = valorTotalProducao.add(contaHospitalar
					.getValorRn());
		}

		if (contaHospitalar.getValorSadt() != null) {
			valorTotalProducao = valorTotalProducao.add(contaHospitalar
					.getValorSadt());
		}

		if (contaHospitalar.getValorHemat() != null) {
			valorTotalProducao = valorTotalProducao.add(contaHospitalar
					.getValorHemat());
		}

		if (contaHospitalar.getValorTransp() != null) {
			valorTotalProducao = valorTotalProducao.add(contaHospitalar
					.getValorTransp());
		}

		if (contaHospitalar.getValorOpm() != null) {
			valorTotalProducao = valorTotalProducao.add(contaHospitalar
					.getValorOpm());
		}

		if (contaHospitalar.getValorProcedimento() != null) {
			valorTotalProducao = valorTotalProducao.add(contaHospitalar
					.getValorProcedimento());
		}

		return valorTotalProducao;
	}

	/**
	 * Copiar todos os dados da conta da FAT_ESPELHOS_ITENS_CONTA_HOSP para a
	 * FAT_ESPELHO_ITENS_CONTA_PROD
	 * 
	 * @param listaEspelhoAih
	 */
	private FatEspelhoItemContaProd copiaEspelhoItensContaParaEspelhoItensContaProd(
			FatEspelhoItemContaHosp espelhoItemContaHosp,
			FatEspelhoContaProd espelhoContaProd) {

		FatEspelhoItemContaProd espelhoItemContaProd = new FatEspelhoItemContaProd(
				espelhoItemContaHosp, espelhoContaProd);

		return espelhoItemContaProd;
	}

	/**
	 * Copiar todos os dados da conta da FAT_ESPELHOS_AIH onde o seqp = 1 para
	 * FAT_ESPELHOS_CONTA_PROD
	 * 
	 * @param listaEspelhoAih
	 * @throws ApplicationBusinessException  
	 */
	private FatEspelhoContaProd copiaEspelhoAihParaEspelhoContaProd(
			FatEspelhoAih espelhoAih, BigDecimal valorTotalProducao,
			DominioSituacaoProducao indSituacaoProd, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		FatEspelhoContaProd esp = new FatEspelhoContaProd(espelhoAih,
				indSituacaoProd, servidorLogado != null ? servidorLogado.getUsuario() : null, valorTotalProducao);

		return esp;

	}

	private Date ultimoDiaMesCompetencia(Date dthrInicioProd) {
		return CoreUtil.obterUltimoDiaDoMes(dthrInicioProd);
	}

	protected FatCompetenciaProdDAO getFatCompetenciaProdDAO() {
		return fatCompetenciaProdDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected FatEspelhoAihDAO getFatEspelhoAihDAO() {
		return fatEspelhoAihDAO;
	}

	protected FatEspelhoItemContaHospDAO getFatEspelhoItemContaHospDAO() {
		return fatEspelhoItemContaHospDAO;
	}

	protected FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}

	protected FatEspelhoContaProdDAO getFatEspelhoContaProdDAO() {
		return fatEspelhoContaProdDAO;
	}

	protected FatEspelhoItemContaProdDAO getFatEspelhoItemContaProdDAO() {
		return fatEspelhoItemContaProdDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}