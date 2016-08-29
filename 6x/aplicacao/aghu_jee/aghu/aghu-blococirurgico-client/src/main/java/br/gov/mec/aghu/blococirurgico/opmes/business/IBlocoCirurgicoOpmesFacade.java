package br.gov.mec.aghu.blococirurgico.opmes.business;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.blococirurgico.vo.ConsultaPreparaOPMEFiltroVO;
import br.gov.mec.aghu.blococirurgico.vo.ConsultarHistoricoProcessoOpmeVO;
import br.gov.mec.aghu.blococirurgico.vo.DemoFinanceiroOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaMateriaisConsumidosVO;
import br.gov.mec.aghu.blococirurgico.vo.ExecutorEtapaAtualVO;
import br.gov.mec.aghu.blococirurgico.vo.GrupoExcludenteVO;
import br.gov.mec.aghu.blococirurgico.vo.InfoProcdCirgRequisicaoOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.ListaMateriaisRequisicaoOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.MateriaisProcedimentoOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.MaterialHospitalarVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcItensRequisicaoOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesListaGrupoExcludente;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.SolicitacaoCompraMaterialVO;
import br.gov.mec.aghu.blococirurgico.vo.VisualizarAutorizacaoOpmeVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.vo.MaterialOpmeVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AghWFEtapa;
import br.gov.mec.aghu.model.AghWFExecutor;
import br.gov.mec.aghu.model.AghWFFluxo;
import br.gov.mec.aghu.model.AghWFHistoricoExecucao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IBlocoCirurgicoOpmesFacade extends Serializable {

	public MbcOpmesVO adicionarOpms(MbcItensRequisicaoOpmes item);

	public List<FatItensProcedHospitalar> consultarProcedimentoSUSVinculadoProcedimentoInterno(Integer pciSeq, Object param);
	
	public MbcRequisicaoOpmes obterRequisicaoOpme(Short seq);
	
	
	//#37054 - Métodos referentes ao fluxo de requisicão OPMEs
	AghWFFluxo iniciarFluxoAutorizacaoOPMEs(RapServidores servidor,MbcRequisicaoOpmes requisicao) throws BaseException;
	void executarEtapaFluxoAutorizacaoOPMEs(RapServidores servidor, AghWFEtapa etapa, String observacao) throws BaseException;
	void rejeitarEtapaFluxoAutorizacaoOPMEs(AghWFEtapa etapa, String justificativa) throws BaseException;	
	void cancelarFluxoAutorizacaoOPMEs(RapServidores servidor, AghWFFluxo fluxo, String justificativa, MbcRequisicaoOpmes requisicao) throws BaseException;

	public void calculaQuantidade(MbcOpmesVO opmeVO);
	
	MbcRequisicaoOpmes obterDetalhesRequisicao(Short requisicaoSeq);

	List<MbcItensRequisicaoOpmesVO> pesquisarMateriaisRequisicao(Short requisicaoSeq, DominioSimNao compativel, DominioSimNao licitado);

	List<ListaMateriaisRequisicaoOpmesVO> pesquisarListaMateriaisRequisicaoOpmes(List<MbcItensRequisicaoOpmesVO> listaMateriais);

	Double calculaCompatibilidade(List<ListaMateriaisRequisicaoOpmesVO> listaMateriais, Double totalCompativel);

	Double calculaIncompatibilidade(List<ListaMateriaisRequisicaoOpmesVO> listaMateriais, Double totalIncompativel);

	List<Integer> buscaDadosProcessoAutorizacaoOpme() throws ApplicationBusinessException;
	
	ExecutorEtapaAtualVO pesquisarExecutorEtapaAtualProcesso(Short requisicaoSeq, RapServidores servidorLogado);

	List<ConsultarHistoricoProcessoOpmeVO> consultarHistorico(Integer seqFluxo);

	public boolean verificaAlteracoes(MbcRequisicaoOpmes requisicaoOpmes, MbcRequisicaoOpmes old) throws ApplicationBusinessException;

	public void concluirRequisicaoOpmes(MbcAgendas agenda,
			AipPacientes aipPacientes, AghUnidadesFuncionais unidadeFuncional, MbcRequisicaoOpmes requisicaoOpmes, boolean persist) throws ApplicationBusinessException;

	public void gravaRequisicao(MbcRequisicaoOpmes requisicaoOpmes);

	public Double atualizaIncompativel(MbcRequisicaoOpmes requisicaoOpmes, StringBuffer incompatibilidadesEncontradas, List<MbcOpmesVO> listaPesquisada);

	public void calculaQuantidades(List<MbcOpmesVO> listaPesquisada);

	public void solicitaMaterial(MbcOpmesVO vo, int i, List<MbcOpmesVO> listaPesquisada, FatItensProcedHospitalar procedimentoSus, List<GrupoExcludenteVO> listaGrupoExcludente);

	public List<MateriaisProcedimentoOPMEVO> consultarMaterialProcedimentoOPME(ConsultaPreparaOPMEFiltroVO filtroVO) throws BaseException;
	
	public void atualizaSituacao(MbcRequisicaoOpmes requisicaoOpmes);

	public MbcRequisicaoOpmes carregaRequisicao(MbcAgendas agenda);

	public MbcOpmesListaGrupoExcludente carregaGrid(MbcRequisicaoOpmes requisicaoOpmes);

	public boolean validaExclusao(MbcOpmesVO opmesVO) throws ApplicationBusinessException;

	public List<MbcOpmesVO> excluir(MbcRequisicaoOpmes requisicaoOpmes, MbcOpmesVO opmesVO, List<MbcOpmesVO> listaPesquisada);
	
	AghWFEtapa obterEtapaPorSeq(Integer etapaSeq);

	void montarDescricaoIncompatibilidade(StringBuffer sb, List<MbcItensRequisicaoOpmesVO> listaMateriais);
	
	List<AghWFExecutor> obterExecutorPorCodigoFluxo(Integer fluxoSeq);
	
//	void rejeitarEtapaFluxoAutorizacaoExecutoresOPMEs(AghWFEtapa etapa, String justificativa) throws ApplicationBusinessException;

	public MbcRequisicaoOpmes copiaRequisicao(MbcRequisicaoOpmes requisicaoOpmes) throws IllegalAccessException, InvocationTargetException;

	public InfoProcdCirgRequisicaoOPMEVO consultarInformacoesProcedimentoCirurgicoAtravesRequisicaoOPME(final Short requisicaoSeqSelecionada) throws BaseException;
	
	public ScoSolicitacaoDeCompra persistirSolicitacaoCompraMaterial(SolicitacaoCompraMaterialVO solicitacaoCompraMaterialVO) throws BaseException;
	
	void concluirJustificativa(Short seqRequisicao, String justificativa);
	
	MbcItensRequisicaoOpmes validarItensRequisicao(MbcItensRequisicaoOpmes itemCorrente, MbcItensRequisicaoOpmes itemRequisicao);

	void validarConcluirMateriaisConsumidos(List<DescricaoCirurgicaMateriaisConsumidosVO> listaMateriaisConsumidos, String justificativa);

	void validaQtdeUtilizada(DescricaoCirurgicaMateriaisConsumidosVO itemMaterialConsumido);

	String montarJustificativaMateriaisConsumidos(Short seqRequisicaoOpme);
	
	public void validaJustificativa(MbcRequisicaoOpmes requisicaoOpmes) throws ApplicationBusinessException;

	List<VisualizarAutorizacaoOpmeVO> carregarVizualizacaoAutorizacaoOpme(Short seqRequisicao) throws ApplicationBusinessException;

	public Integer verificarPrazoAgenda(MbcAgendas agenda, MbcRequisicaoOpmes requisicaoOpmes) throws ApplicationBusinessException;

	public MbcRequisicaoOpmes obterRequisicaoOriginal(Short short1);

	public Boolean permiteAlteracaoRequisicao(MbcRequisicaoOpmes requisicaoOpmes);

	public String getEtapaAutorizacao(MbcRequisicaoOpmes requisicaoOpmes);
	
	void insereMbcItensRequisicaoOpmesJn(MbcItensRequisicaoOpmes opme, DominioOperacoesJournal operacao);
	
	void insereMbcRequisicaoOpmes(MbcRequisicaoOpmes opme, DominioOperacoesJournal operacao);
	
	void insereMbcMateriaisItemOpmesJn(MbcMateriaisItemOpmes opme, DominioOperacoesJournal operacao);
	
	void validaRequisicaoEscala(MbcRequisicaoOpmes requisicaoOpmes) throws ApplicationBusinessException;
	
	Boolean isRequisicaoEscalada(MbcRequisicaoOpmes requisicaoOpmes);
	
	Boolean isRequisicaoOpmeIncompativel(MbcRequisicaoOpmes requisicaoOpmes);
	
	Boolean isRequisicaoOpmeIncompativelSemEscala(MbcRequisicaoOpmes requisicaoOpmes);
	
	Boolean isRequisicaoFinalizada(MbcRequisicaoOpmes requisicaoOpmes);
	
	Boolean isRequisicaoAndamento(MbcRequisicaoOpmes requisicaoOpmes);

	List<MaterialHospitalarVO> pesquisarMaterialHospitalar(String matNome) throws BaseException ;

	List<DemoFinanceiroOPMEVO> pesquisaDemonstrativoFinanceiroOpmes(
			Date competenciaInicial,Date competenciaFinal, Short especialidadeSeq, Integer prontuario,
			Integer matCodigo) throws BaseException ;


	Double atualizaTotalCompativel(List<MbcOpmesVO> listaPesquisada);

	void setCompatibilidadeGrupoExcludencia(List<MbcOpmesVO> listaPesquisada);

	void cancelarOpmeSemFluxo(MbcRequisicaoOpmes requisicaoOpmes)
			throws BaseException;

	void gravaRequisicaoFull(MbcRequisicaoOpmes requisicaoOpmes,
			List<MbcItensRequisicaoOpmes> itensExcluidos,
			List<MbcOpmesVO> listaClone, Boolean zeraFluxo)
			throws ApplicationBusinessException;

	Boolean verificarConvenio(MbcAgendas agenda);

	MbcAgendas atualizarConvenio(MbcAgendas agenda) throws ApplicationBusinessException;

	MbcItensRequisicaoOpmes criaRequisicaoAdicional(
			MbcRequisicaoOpmes requisicao, MaterialOpmeVO materialVO,
			String solicitacaoMaterial, Integer qtdeSolicitada);

	List<AghWFHistoricoExecucao> buscarHistExecutoresAutorizacao(
			AghWFEtapa etapa);

	InfoProcdCirgRequisicaoOPMEVO consultarInformacoesProcedimentoCirurgicoAtravesRequisicaoOPMESemSituacao(
			Short requisicaoSeqSelecionada) throws BaseException;

	void concluirMateriaisConsumidos(
			List<DescricaoCirurgicaMateriaisConsumidosVO> itemLista);

	MbcOpmesListaGrupoExcludente consultaItensProcedimento(Integer pciSeq,
			FatItensProcedHospitalar procedimentoSus,
			MbcRequisicaoOpmes requisicaoOpmes, Date dtAgenda);
}