package br.gov.mec.aghu.estoque.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoeJn;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoes;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;


@Stateless
public class ScoMateriaisClassificacaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ScoMateriaisClassificacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;

	/*
	 * Métodos para Inserir ScoMateriaisClassificacoes
	 */

	private static final long serialVersionUID = -8356433858186091238L;

	/**
	 * ORADB SCOT_MCL_BRI (INSERT)
	 * @param materiaisClassificacoes
	 * @throws ApplicationBusinessException  
	 */
	private void preInserir(ScoMateriaisClassificacoes materiaisClassificacoes) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		materiaisClassificacoes.setServidor(servidorLogado);
		materiaisClassificacoes.setCriadoEm(new Date());
	}

	/**
	 * Inserir ScoMateriaisClassificacoes
	 * @param materiaisClassificacoes
	 * @throws BaseException
	 */
	public void inserir(ScoMateriaisClassificacoes materiaisClassificacoes, String nomeMicrocomputador) throws BaseException {
		this.preInserir(materiaisClassificacoes);
		this.getComprasFacade().persistirScoMateriaisClassificacoes(materiaisClassificacoes);
		this.posInserir(materiaisClassificacoes, nomeMicrocomputador);
	}

	/**
	 * ORADB TRIGGER SCOT_MCL_ARI
	 * @param materiaisClassificacoes
	 * @throws BaseException 
	 * @throws IllegalStateException 
	 */
	private void posInserir(ScoMateriaisClassificacoes materiaisClassificacoes, String nomeMicrocomputador) throws IllegalStateException, BaseException {

		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CLASSIFICAO_MATERIAL_NIVEL5);

		if (parametro != null && parametro.getVlrTexto() != null) {

			String cn5Numero = materiaisClassificacoes.getId().getCn5Numero().toString();
			String vlrTexto = parametro.getVlrTexto();

			if (vlrTexto.contains(cn5Numero.substring(0, 1)) && vlrTexto.contains(cn5Numero.substring(2, 3))) {

				AfaMedicamento medicamento = getFarmaciaFacade().obterMedicamento(materiaisClassificacoes.getMaterial().getCodigo());

				if (medicamento != null) {

					medicamento.setIndRevisaoCadastro(Boolean.TRUE);
					this.getFarmaciaApoioFacade().efetuarAlteracao(medicamento, nomeMicrocomputador, new Date());

				}

			}

			if (vlrTexto.contains(cn5Numero.substring(0, 1))) {

				if (!getFaturamentoFacade().verificaProcedimentoHospitalarInterno(materiaisClassificacoes.getId().getMatCodigo())) {

					FatProcedHospInternos fatProcedHospInternos = new FatProcedHospInternos();
					fatProcedHospInternos.setMaterial(materiaisClassificacoes.getMaterial());
					fatProcedHospInternos.setDescricao(materiaisClassificacoes.getMaterial().getNome());
					fatProcedHospInternos.setSituacao(DominioSituacao.A);
					getFaturamentoFacade().inserirFatProcedHospInternos(fatProcedHospInternos);

				} else {

					FatProcedHospInternos fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(materiaisClassificacoes.getMaterial().getCodigo(), FatProcedHospInternos.Fields.MAT_CODIGO);
					fatProcedHospInternos.setSituacao(DominioSituacao.A);
					getFaturamentoFacade().atualizarFatProcedHospInternos(fatProcedHospInternos);

				}

			}

		}

	}

	/*
	 * Métodos para Remover ScoMateriaisClassificacoes
	 */
	
	/**
	 * Remover ScoMateriaisClassificacoes
	 * @param materiaisClassificacoes
	 * @throws BaseException
	 */
	public void remover(ScoMateriaisClassificacoes materiaisClassificacoes) throws BaseException {

		getComprasFacade().removerScoMateriaisClassificacoes(materiaisClassificacoes);
		posRemover(materiaisClassificacoes);

	}

	/**
	 * ORADB TRIGGER SCOT_MCL_ARD
	 * @param materiaisClassificacoes
	 *  
	 * @throws ApplicationBusinessException 
	 */
	private void posRemover(ScoMateriaisClassificacoes materiaisClassificacoes) throws ApplicationBusinessException {

		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CLASSIFICAO_MATERIAL_NIVEL5);

		if (parametro != null && parametro.getVlrTexto() != null) {

			String cn5Numero = materiaisClassificacoes.getId().getCn5Numero().toString();
			String vlrTexto = parametro.getVlrTexto();

			if (vlrTexto.contains(cn5Numero)) {

				if (getFaturamentoFacade().verificaProcedimentoHospitalarInterno(materiaisClassificacoes.getId().getMatCodigo())) {

					FatProcedHospInternos fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(materiaisClassificacoes.getMaterial().getCodigo(), FatProcedHospInternos.Fields.MAT_CODIGO);
					fatProcedHospInternos.setSituacao(DominioSituacao.I);
					getFaturamentoFacade().atualizarFatProcedHospInternos(fatProcedHospInternos);

				}

			}

		}

		insereJn(materiaisClassificacoes);

	}

	/**
	 * 
	 * @param materiaisClassificacoes
	 * @throws ApplicationBusinessException  
	 */
	private void insereJn(ScoMateriaisClassificacoes materiaisClassificacoes) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		ScoMateriaisClassificacoeJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, ScoMateriaisClassificacoeJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		jn.setServidorAlterado(servidorLogado);
		jn.setCn5Numero(materiaisClassificacoes.getId().getCn5Numero());
		jn.setMatCodigo(materiaisClassificacoes.getMaterial().getCodigo());
		jn.setServidor(materiaisClassificacoes.getServidor());
		getComprasFacade().inserirScoMateriaisClassificacoeJn(jn);
	}

	/**
	 * Getters para RNs e DAOs
	 */

	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}
	
	protected IFarmaciaApoioFacade getFarmaciaApoioFacade() {
		return farmaciaApoioFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
