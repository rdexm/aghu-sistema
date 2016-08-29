package br.gov.mec.aghu.exames.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioTipoTransporteUnidade;
import br.gov.mec.aghu.exames.contratualizacao.vo.ItemContratualizacaoVO;
import br.gov.mec.aghu.exames.ejb.AmostraExameBeanLocal;
import br.gov.mec.aghu.exames.ejb.AnexarDocumentoLaudoBeanLocal;
import br.gov.mec.aghu.exames.ejb.ArquivoLaudoResultadoExameBeanLocal;
import br.gov.mec.aghu.exames.ejb.CancelaExameBeanLocal;
import br.gov.mec.aghu.exames.ejb.SolicitacaoExameBeanLocal;
import br.gov.mec.aghu.exames.ejb.VoltarProtocoloUnicoBeanLocal;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAndamentoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameCancelamentoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaVO;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelDocResultadoExame;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

@Stateless
@SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
public class ExamesBeanFacade extends BaseFacade implements IExamesBeanFacade {

	private static final long serialVersionUID = 2182490764434405440L;

	@EJB
	private SolicitacaoExameBeanLocal solicitacaoExameBeanLocal;

	@EJB
	private AmostraExameBeanLocal amostraExameBeanLocal;

	@EJB
	private ArquivoLaudoResultadoExameBeanLocal arquivoLaudoResultadoExameBeanLocal;

	@EJB
	private CancelaExameBeanLocal cancelaExameBeanLocal;

	@EJB
	private AnexarDocumentoLaudoBeanLocal anexarDocumentoLaudoBeanLocal;

	@EJB
	private VoltarProtocoloUnicoBeanLocal voltarProtocoloUnicoBean;

	@Override
	public SolicitacaoExameVO gravaSolicitacaoExame(SolicitacaoExameVO solicitacaoExameVO, String nomeMicrocomputador) throws BaseException {
		return this.solicitacaoExameBeanLocal.gravaSolicitacaoExame(solicitacaoExameVO, nomeMicrocomputador);
	}

	@Override
	public List<ItemContratualizacaoVO> gravaSolicitacaoExameContratualizacao(AelSolicitacaoExames aelSolicitacaoExames,
			List<ItemContratualizacaoVO> listaItensVO, String nomeMicrocomputador) throws BaseException {
		return this.solicitacaoExameBeanLocal
				.gravaSolicitacaoExameContratualizacao(aelSolicitacaoExames, listaItensVO, nomeMicrocomputador);
	}

	@Override
	@Secure("#{s:hasPermission('receberAmostra','executar')}")
	public 	ImprimeEtiquetaVO receberAmostra(AghUnidadesFuncionais unidadeExecutora,
			AelAmostras amostra, String nroFrascoFabricante,
			List<ExameAndamentoVO> listaExamesAndamento,
			String nomeMicrocomputador) throws BaseException {
		return this.amostraExameBeanLocal.receberAmostra(unidadeExecutora, amostra, nroFrascoFabricante, listaExamesAndamento, nomeMicrocomputador);
	}

	@Override
	@Secure("#{s:hasPermission('receberAmostra','executar')}")
	public ImprimeEtiquetaVO receberAmostraSolicitacao(
			AghUnidadesFuncionais unidadeExecutora, AelAmostras amostra,
			List<ExameAndamentoVO> listaExamesAndamento,
			String nomeMicrocomputador) throws BaseException {
		//return this.amostraExameBeanLocal.receberAmostraSolicitacao(unidadeExecutora, amostra, configExameOrigem, numeroApOrigem,nomeMicrocomputador);
		return this.amostraExameBeanLocal.receberAmostraSolicitacao(
				unidadeExecutora, amostra, listaExamesAndamento, nomeMicrocomputador);
	}

    @Override
	@Secure("#{s:hasPermission('voltarTodasAmostras','executar')}")
	public boolean voltarSituacaoAmostraSolicitacao(AghUnidadesFuncionais unidadeExecutora, AelAmostras amostra, String nomeMicrocomputador)
			throws BaseException {
		return this.amostraExameBeanLocal.voltarSituacaoAmostraSolicitacao(unidadeExecutora, amostra, nomeMicrocomputador);
	}

	@Override
	@Secure("#{s:hasPermission('receberAmostra','executar')}")
	public boolean voltarAmostra(AghUnidadesFuncionais unidadeExecutora, AelAmostras amostra, String nomeMicrocomputador)
			throws BaseException {
		return this.amostraExameBeanLocal.voltarAmostra(unidadeExecutora, amostra, nomeMicrocomputador);
	}

	@Override
	@Secure("#{s:hasPermission('recepcionarPaciente','executar')}")
	public void receberItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador)
			throws BaseException {
		this.solicitacaoExameBeanLocal.receberItemSolicitacaoExame(aelItemSolicitacaoExames, nomeMicrocomputador);
	}

	@Override
	@Secure("#{s:hasPermission('recepcionarPaciente','executar')}")
	public void voltarItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador)
			throws BaseException {
		this.solicitacaoExameBeanLocal.voltarItemSolicitacaoExame(aelItemSolicitacaoExames, nomeMicrocomputador);
	}

	@Override
	@BypassInactiveModule
	/*
	 * TODO Este bypass esta sendo colocado para uso do AGHU na UBS do HCPA,
	 * ainda eh necessario validar se ele realmente ficara aqui
	 */
	public void atualizarItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador)
			throws BaseException {
		this.solicitacaoExameBeanLocal.atualizarItemSolicitacaoExame(aelItemSolicitacaoExames, nomeMicrocomputador);
	}
	
	@Override
	@BypassInactiveModule
	public void atualizarItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador)
			throws BaseException {
		this.solicitacaoExameBeanLocal.atualizarItemSolicitacaoExame(aelItemSolicitacaoExames, itemSolicitacaoExameOriginal, nomeMicrocomputador);
	}	

	@Override
	public void recepcionarPaciente(AelItemSolicitacaoExames itemSolicitacaoExames, final DominioTipoTransporteUnidade transporte,
			final Boolean indUsoO2Un, String nomeMicrocomputador) throws BaseException {
		this.solicitacaoExameBeanLocal.recepcionarPaciente(itemSolicitacaoExames, transporte, indUsoO2Un, nomeMicrocomputador);
	}

	@Override
	public void inserirAelDocResultadoExame(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador)
			throws BaseException {
		this.arquivoLaudoResultadoExameBeanLocal.inserirAelDocResultadoExame(doc, unidadeExecutora, nomeMicrocomputador);
	}

	@Override
	public void removerAelDocResultadoExame(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador)
			throws BaseException {
		this.arquivoLaudoResultadoExameBeanLocal.removerAelDocResultadoExame(doc, unidadeExecutora, nomeMicrocomputador);
	}

	@Override
	public void excluirItensExamesSelecionados(List<ItemSolicitacaoExameCancelamentoVO> itens, String nomeMicrocomputador)
			throws BaseException {
		this.cancelaExameBeanLocal.excluirItensExamesSelecionados(itens, nomeMicrocomputador);
	}

	@Override
	public void cancelarExames(AelItemSolicitacaoExames aelItemSolicitacaoExames, final AelMotivoCancelaExames motivoCancelar,
			String nomeMicrocomputador) throws BaseException {
		this.cancelaExameBeanLocal.cancelarExames(aelItemSolicitacaoExames, motivoCancelar, nomeMicrocomputador);
	}

	/**
	 * SEM PERMISSÃO ( Estornar cancelamento de exame na área executora e na
	 * coleta utilizam o mesmo método, estorias 5459 e 5991 )
	 */
	@Override
	public void estornarItemSolicitacaoExame(AelItemSolicitacaoExames item, String nomeMicrocomputador) throws BaseException {
		this.cancelaExameBeanLocal.estornarItemSolicitacaoExame(item, nomeMicrocomputador);
	}

	@Override
	public AelSolicitacaoExames atualizarSolicitacaoExame(AelSolicitacaoExames solicExame,
			List<AelItemSolicitacaoExames> itemSolicExameExcluidos, String nomeMicrocomputador) throws BaseException {
		return this.solicitacaoExameBeanLocal.atualizarSolicitacaoExame(solicExame, itemSolicExameExcluidos, nomeMicrocomputador);
	}

	@Override
	@Secure("#{s:hasPermission('anexarDocLaudoAutomatic','executar')}")
	public void anexarDocumentoLaudo(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora) throws BaseException {
		this.anexarDocumentoLaudoBeanLocal.anexarDocumentoLaudo(doc, unidadeExecutora);
	}

	@Override
	public void removerDocumentoLaudo(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora) throws BaseException {
		this.anexarDocumentoLaudoBeanLocal.removerDocumentoLaudo(doc, unidadeExecutora);
	}

	@Override
	public void processarComunicacaoModuloGestaoAmostra(String nomeMicrocomputador) throws BaseException {
		this.voltarProtocoloUnicoBean.processarComunicacaoModuloGestaoAmostra(nomeMicrocomputador);
	}

	// #25907
	@Override
	public void atualizarSituacaoExamesAmostra(AelAmostras amostra, String nomeMicrocomputador) throws BaseException {
		amostraExameBeanLocal.atualizarSituacaoExamesAmostra(amostra, nomeMicrocomputador);
	}

	// #25907
	@Override
	public void atualizarSituacaoExamesAmostraColetada(AelAmostras amostra, String nomeMicrocomputador) throws BaseException {
		amostraExameBeanLocal.atualizarSituacaoExamesAmostraColetada(amostra, nomeMicrocomputador);
	}

}