package br.gov.mec.aghu.business.bancosangue;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.LockMode;

import br.gov.mec.aghu.bancosangue.vo.AtualizaCartaoPontoVO;
import br.gov.mec.aghu.bancosangue.vo.ItemSolicitacaoHemoterapicaVO;
import br.gov.mec.aghu.dominio.DominioMcoType;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoColeta;
import br.gov.mec.aghu.dominio.DominioTipoPaciente;
import br.gov.mec.aghu.faturamento.vo.DoacaoColetaSangueVO;
import br.gov.mec.aghu.faturamento.vo.DoadorSangueTriagemClinicaVO;
import br.gov.mec.aghu.model.AbsAmostrasPacientes;
import br.gov.mec.aghu.model.AbsAmostrasPacientesId;
import br.gov.mec.aghu.model.AbsCandidatosDoadores;
import br.gov.mec.aghu.model.AbsComponentePesoFornecedor;
import br.gov.mec.aghu.model.AbsComponentePesoFornecedorId;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsDoacoes;
import br.gov.mec.aghu.model.AbsEstoqueComponentes;
import br.gov.mec.aghu.model.AbsExameComponenteVisualPrescricao;
import br.gov.mec.aghu.model.AbsFornecedorBolsas;
import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativa;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativaId;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicasId;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsMovimentosComponentes;
import br.gov.mec.aghu.model.AbsOrientacaoComponentes;
import br.gov.mec.aghu.model.AbsOrientacaoComponentesId;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.AbsRegSanguineoPacientes;
import br.gov.mec.aghu.model.AbsSolicitacoesDoacoes;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesPorAmostra;
import br.gov.mec.aghu.model.AbsValidAmostrasComponenteId;
import br.gov.mec.aghu.model.AbsValidAmostrasComponentes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.VAbsMovimentoComponente;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;

public interface IBancoDeSangueFacade extends Serializable {
	
	/**
	 * Busca por pk um objeto de AbsItemSolicitacaoHemoterapicaJustificativa.
	 * 
	 * @param id
	 * @return
	 */
	AbsItemSolicitacaoHemoterapicaJustificativa getAbsItemSolicitacaoHemoterapicaJustificativa(AbsItemSolicitacaoHemoterapicaJustificativaId id);


	public String buscaJustificativaLaudoCsa(final Integer atdSeq,
			final Integer phiSeq);

	public String buscaJustificativaLaudoPhe(final Integer atdSeq,
			final Integer phiSeq);

	public AtualizaCartaoPontoVO atualizaCartaoPontoServidor()
			throws ApplicationBusinessException;

	public AbsComponenteSanguineo obterComponeteSanguineoPorCodigo(
			final String codigo);

	public AbsProcedHemoterapico obterProcedHemoterapicoPorCodigo(
			final String codigo);

	public void excluirItemSolicitacaoHemoterapica(
			final AbsItensSolHemoterapicas itensSolHemoterapicas)
			throws ApplicationBusinessException;

	public AbsItensSolHemoterapicas inserirItemSolicitacaoHemoterapica(
			final AbsItensSolHemoterapicas itemSolHemoterapica)
			throws BaseException;

	public AbsItensSolHemoterapicas atualizarItemSolicitacaoHemoterapica(
			final AbsItensSolHemoterapicas itensSolHemoterapica)
			throws BaseException;

	public AbsItemSolicitacaoHemoterapicaJustificativa atualizarJustificativaItemSolicitacaoHemoterapica(
			final AbsItemSolicitacaoHemoterapicaJustificativa item)
			throws ApplicationBusinessException;

	public AbsItemSolicitacaoHemoterapicaJustificativa inserirJustificativaItemSolicitacaoHemoterapica(
			final AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa)
			throws ApplicationBusinessException;

	public void excluirJustificativaItemSolicitacaoHemoterapica(
			final AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa)
			throws ApplicationBusinessException;

	public AbsSolicitacoesHemoterapicas atualizarSolicitacaoHemoterapica(
			final AbsSolicitacoesHemoterapicas solicitacaoHemoterapica, String nomeMicrocomputador)
			throws BaseException;

	public void inserirSolicitacaoHemoterapica(
			final AbsSolicitacoesHemoterapicas solicitacaoHemoterapica, String nomeMicrocomputador)
			throws ApplicationBusinessException;

	/**
	 * Remove todas as solicitações hemoterápicas de um atendimento.
	 * 
	 * @param atendimento
	 */
	public void excluirSolicitacoesHemoterapicasPorAtendimeto(
			final AghAtendimentos atendimento);

	public void excluirSolicitacaoHemoterapica(
			final AbsSolicitacoesHemoterapicas solicitacaoHemoterapica)
			throws ApplicationBusinessException;

	public List<AbsProcedHemoterapico> listarAbsProcedHemoterapicos(
			final Object objPesquisa);

	public Long listarAbsProcedHemoterapicoCount(final Object objPesquisa);

	public List<AbsComponenteSanguineo> listarAbsComponenteSanguineos(
			final Object objPesquisa);

	public Long listarAbsComponenteSanguineosCount(final Object objPesquisa);

	public void cancelaAmostrasHemoterapicas(final AghAtendimentos atendimento,
			final AinTiposAltaMedica tipoAltaMedica, String nomeMicrocomputador)
			throws ApplicationBusinessException;

	
	public Long pesquisarCandidatosDoadoresPorNacionalidadeCount(
			final AipNacionalidades nacionalidade);

	
	public Long pesquisarCandidatosDoadoresPorOcupacaoCount(
			final AipOcupacoes ocupacao);

	
	public List<VAbsMovimentoComponente> pesquisarItensSolHemoterapicasPOL(
			final Integer codigoPaciente);

	public List<AbsGrupoJustificativaComponenteSanguineo> pesquisarAbsGrupoJustificativaComponenteSanguineo(
			int firstResult, int maxResults, String orderProperty, Boolean asc,
			Short seq, String descricao, DominioSituacao situacao, String titulo);

	public Long pesquisarAbsGrupoJustificativaComponenteSanguineoCount(
			Short seq, String descricao, DominioSituacao situacao, String titulo);

	public AbsGrupoJustificativaComponenteSanguineo obterGrupoJustificativaComponenteSanguineo(
			Short seq);

	public void persistirGrupoJustificativaComponenteSanguineo(
			AbsGrupoJustificativaComponenteSanguineo grupo) throws ApplicationBusinessException;

	public void persistirJustificativaComponenteSanguineo(
			AbsJustificativaComponenteSanguineo justificativa)
			throws BaseListException, ApplicationBusinessException;

	public List<AbsProcedHemoterapico> listarAbsProcedHemoterapicos(
			String codigo, String descricao, Boolean indAmostra,
			Boolean indJustificativa, DominioSituacao indSituacao,
			int firstResult, int maxResults);

	public Long listarAbsProcedHemoterapicosCount(String codigo,
			String descricao, Boolean indAmostra, Boolean indJustificativa,
			DominioSituacao indSituacao);

	List<AbsJustificativaComponenteSanguineo> pesquisarJustificativaComponenteSanguineosPorGrupoJustificativaCompomenteSanguineo(
			Short seqGrupoJustificativaComponenteSanguineo);

	void persistirProcedimentoHemoterapico(
			AbsProcedHemoterapico procedimento) throws ApplicationBusinessException,
			ApplicationBusinessException, BaseException;

	void excluirProcedimentoHemoterapico(String codigo) throws ApplicationBusinessException;

	AbsProcedHemoterapico obterProcedimentoHemoterapicoPorCodigo(String codigo);

	List<AbsProcedHemoterapico> pesquisarProcedimentoHemoterapicoPorCodigoDescricao(Object param);

	AbsJustificativaComponenteSanguineo obterAbsJustificativaComponenteSanguineo(AbsJustificativaComponenteSanguineo justificativa);

	List<AbsJustificativaComponenteSanguineo> pesquisarJustificativaUsoHemoterapico(
			Integer seq,
			String codigoComponenteSanguineo,
			String codigoProcedimentoHemoterapico,
			Short seqGrupoJustificativaComponenteSanguineo,
			DominioTipoPaciente tipoPaciente, DominioSimNao descricaoLivre,
			DominioSituacao situacao, String descricao, int firstResult, int maxResult);
	
	public Long pesquisarJustificativaUsoHemoterapicoCount(
			  Integer seq,
			  String codigoComponenteSanguineo,
			  String codigoProcedimentoHemoterapico,
			  Short seqGrupoJustificativaComponenteSanguineo,
			  DominioTipoPaciente tipoPaciente,
			  DominioSimNao descricaoLivre,
			  DominioSituacao situacao,
			  String descricao);
	
	public List<AbsGrupoJustificativaComponenteSanguineo> pesquisarGrupoJustificativaComponenteSanguineo(String param);
	
	public AbsJustificativaComponenteSanguineo obterAbsJustificativaComponenteSanguineoPorSeq(Integer seq, Enum... fetchArgs);

	public List<AbsSolicitacoesDoacoes> pesquisarAbsSolicitacoesDoacoes(
			Integer pacCodigo);

	public void removerAbsSolicitacoesDoacoes(
			AbsSolicitacoesDoacoes absSolicitacoesDoacoes, boolean flush);

	public Short obterMaxSeqAbsSolicitacoesDoacoes(Integer pacCodigo);

	public AbsSolicitacoesDoacoes inserirAbsSolicitacoesDoacoes(
			AbsSolicitacoesDoacoes absSolicitacoesDoacoes, boolean flush);

	public List<AbsSolicitacoesPorAmostra> pesquisarAbsSolicitacoesPorAmostra(
			Integer pacCodigo);

	public List<AbsRegSanguineoPacientes> listarRegSanguineoPacientesPorCodigoPaciente(
			Integer pacCodigo);

	public void persistirAbsRegSanguineoPacientes(
			AbsRegSanguineoPacientes absRegSanguineoPacientes);

	public List<AbsMovimentosComponentes> listarMovimentosComponentesPorCodigoPaciente(
			Integer pacCodigo);

	public void persistirAbsMovimentosComponentes(
			AbsMovimentosComponentes absMovimentosComponentes);

	public void persistirAbsEstoqueComponentes(
			AbsEstoqueComponentes absEstoqueComponentes);

	public List<AbsEstoqueComponentes> listarEstoqueComponentesPorCodigoPaciente(
			Integer pacCodigo);

	public void atualizarAbsDoacoes(AbsDoacoes absDoacoes, boolean flush);

	public AbsAmostrasPacientes obterAbsAmostrasPacientesPorChavePrimaria(
			AbsAmostrasPacientesId id);

	public void persistirAbsAmostrasPacientes(
			AbsAmostrasPacientes absAmostrasPacientes);

	public void desatacharAbsAmostrasPacientes(
			AbsAmostrasPacientes absAmostrasPacientes);

	public void removerAbsAmostrasPacientes(
			AbsAmostrasPacientes absAmostrasPacientes, boolean flush);

	public List<AbsAmostrasPacientes> pesquisarAmostrasPaciente(
			Integer pacCodigo);

	public List<ItemSolicitacaoHemoterapicaVO> obterListaItemSolicitacaoHemoterapicaVO(Integer atdSeq, Integer seq);	

	public AbsItensSolHemoterapicas obterItemSolicitacaoHemoterapicaVO(AbsItensSolHemoterapicasId id);
	
	public List<AbsItensSolHemoterapicas> pesquisarItensHemoterapiaComponentesSanguineos(Integer sheAtdSeq, Integer sheSeq);
	
	
	public void refreshSolicitacoesHeoterapicas(AbsSolicitacoesHemoterapicas elemento);	
	
	public List<AbsSolicitacoesHemoterapicas> pesquisarSolicitacoesHemoterapicasRelatorio(
			MpmPrescricaoMedica prescricaoMedica);
	
	public List<AbsItensSolHemoterapicas> pesquisarItensHemoterapiaProcedimentos(Integer sheAtdSeq, Integer sheSeq);
	
	public List<AbsItemSolicitacaoHemoterapicaJustificativa> pesquisarJustificativasItemHemoterapia(Integer sheAtdSeq,
			Integer sheSeq, Short ishSequencia);
	
	public void refreshAbsItemSolicitacaoHemoterapicaJustificativa(AbsItemSolicitacaoHemoterapicaJustificativa elemento);
	
	public List<AbsComponenteSanguineo> obterComponetesSanguineosAtivos();
	
	public List<AbsProcedHemoterapico> obterProcedHemoterapicosAtivos();	
	
	public List<AbsSolicitacoesHemoterapicas> pesquisarTodasHemoterapiasPrescricaoMedica(
			MpmPrescricaoMedicaId id);
	
	public List<AbsJustificativaComponenteSanguineo> listarJustificativasPadraoDoComponenteOuProcedimento(
			final String procedHemoterapicoCodigo, final String componenteSanguineoCodigo,
			final Short seqGrupoJustificativaComponenteSanguineo,
			final Boolean indPacPediatrico);
	
	public List<AbsGrupoJustificativaComponenteSanguineo> listarGruposJustifcativasDoComponenteOuProcedimento(
			final String procedHemoterapicoCodigo, final String componenteSanguineoCodigo);	
	
	AbsSolicitacoesHemoterapicas obterSolicitacoesHemoterapicas(
			final Integer atdSeq, final Integer seq);
	
	AbsSolicitacoesHemoterapicas obterSolicitacoesHemoterapicasComItensSolicitacoes(
			final Integer atdSeq, final Integer seq);
	
	public String buscarDescricaoJustificativaSolicitacao(
			final String procedHemoterapicoCodigo, final String componenteSanguineoCodigo);
	
	public List<AbsComponenteSanguineo> listaComponentesSanguineos(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, AbsComponenteSanguineo componenteSanguineo);

	public Long pesquisarComponentesSanguineosCount(AbsComponenteSanguineo absComponentesSanguineo);
	
	public Long obterComponenteSanguineosCount(Object param);
	
	public List<AbsComponenteSanguineo> obterComponenteSanguineos(Object param);
	
	public Long pesquisarFornecedorCount(Object obj); 
	
	public AbsComponenteSanguineo obterAbsComponenteSanguineoPorId(String codigo);
	
	public List<AbsExameComponenteVisualPrescricao> listaExameComponenteVisualPrescricao(Integer firstResult,Integer maxResult, String orderProperty, boolean asc,
			AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao);
	
	public Long listaExameComponenteVisualPrescricaoCount(AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao);
	
	public AbsComponenteSanguineo obterComponenteSanguineoUnico(String param);
	
	public AbsExameComponenteVisualPrescricao obterAbsExameComponenteVisualPrescricaoPorId(Integer id);
	
	AbsExameComponenteVisualPrescricao obterAbsExameComponenteVisualPrescricaoPorId(Integer id, Enum ...fields);
	
	public List<AbsOrientacaoComponentes> listaOrientacoes(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, final Short seq, final String descricao, final DominioSituacao situacao, final String componenteSaguineo);
	
	public Long pesquisarOrientacaoComponentesCount(final Short seq, final String descricao, final DominioSituacao situacao, final String componenteSaguineo);

	public AbsOrientacaoComponentes obterAbsOrientacaoComponentesPorId(AbsOrientacaoComponentesId id,  Boolean left, Enum... fetchEnums);

	public void atualizarAbsOrientacaoComponentes(AbsOrientacaoComponentes absOrientacaoComponentes) throws ApplicationBusinessException;

	public void manterAbsOrientacaoComponentes(AbsOrientacaoComponentes absOrientacaoComponentes) throws ApplicationBusinessException;
	
	public AbsExameComponenteVisualPrescricao obterAbsExameComponenteVisualPrescricaoId(Integer id);

	public List<AbsComponentePesoFornecedor> listaPesoFornecedor(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, AbsComponentePesoFornecedor componentePesoFornecedor);

	public Long pesquisarComponentePesoFornecedorCount(AbsComponentePesoFornecedor componentePesoFornecedor);
	
	public List<AbsFornecedorBolsas> pesquisarFornecedor(Object obj);

	public void verificaInativo(AbsComponenteSanguineo absComponenteSanguineo)throws ApplicationBusinessException;

	public void verificaJustificativa(AbsComponenteSanguineo absComponenteSanguineo)throws ApplicationBusinessException;	
	
	public void gravarRegistro(AbsComponenteSanguineo absComponenteSanguineo, Boolean edita) throws ApplicationBusinessException;
	
	
	public List<AbsSolicitacoesHemoterapicas> buscaSolicitacoesHemoterapicas(MpmPrescricaoMedicaId id, Boolean listarTodas);
	
	public List<DoacaoColetaSangueVO> listarRegSanguineoPacienteComExistsSemAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, DominioOrigemAtendimento origem);
	
	
	public void refreshAbsItensSolHemoterapicas(AbsItensSolHemoterapicas elemento);
	
	public List<AbsItensSolHemoterapicas> pesquisarItensHemoterapia(Integer sheAtdSeq, Integer sheSeq);
	
	public void removerAbsItensSolHemoterapicas(AbsItensSolHemoterapicas elemento);	
	
	public List<AbsSolicitacoesHemoterapicas> pesquisarHemoterapiasParaCancelar(
			Integer atdSeq, Integer pmeSeq, Date dthrMovimento);
	
	public void removerAbsItemSolicitacaoHemoterapicaJustificativa(AbsItemSolicitacaoHemoterapicaJustificativa elemento);	
	
	public List<AbsAmostrasPacientes> amostrasPaciente(AipPacientes paciente, Date dthrInicio);
	
	public List<AbsEstoqueComponentes> listarEstoquesComponentes(Integer pacCodigo, Date dthrInicio);
	
	public List<AbsMovimentosComponentes> listarMovimentosComponentes(Integer pacCodigo, Date dthrInicio);
	
	public void substituirPacienteRegSanguineoPacientes(AipPacientes pacienteOrigem, AipPacientes pacienteDestino, Date dthrInicio);
	
	public List<AbsSolicitacoesDoacoes> listarSolicitacoesDoacoes(Integer pacCodigo, Date dthrSolicitacao, LockMode lockMode);
	
	public Short obterSequenciaSolicitacoesDoacoes(Integer pacCodigo);
	
	public AbsSolicitacoesHemoterapicas obterSolicitacaoPorSeqESitColeta(Integer seq, DominioSituacaoColeta sitColeta);
	
	public List<DoacaoColetaSangueVO> listarDoacaoPorPeriodo(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo);
		
	public List<DoacaoColetaSangueVO> listarDoacaoPorTipo(Date vDtHrInicio, Date vDtHrFim,
			String tipo, Integer matricula, Short vinCodigo);
			
	public List<DoacaoColetaSangueVO> listarDoacaoColetaSangue(Date vDtHrInicio, Date vDtHrFim, 
			String tipo, Integer matricula, Short vinCodigo);
	
	public List<DoadorSangueTriagemClinicaVO> listarDoadorSangueTriagemClinica(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo) ;
	
	public List<DoacaoColetaSangueVO> listarMovimentosComponentesSemExistsComAgrupamentoIrradiado(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, DominioMcoType mcoType, 
			Boolean indEstorno, Boolean indIrradiado);
		
	public List<DoacaoColetaSangueVO> listarTransfusaoComExistsSemAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo, DominioOrigemAtendimento origem);	
	
	public List<DoacaoColetaSangueVO> listarTransfusaoSemExistsSemAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo);
	
	
	public List<DoacaoColetaSangueVO> listarTransfusaoComExistsComAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo, DominioOrigemAtendimento origem);
	
	public List<DoacaoColetaSangueVO> listarTransfusaoSemExistsComAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo);
	
	public List<DoacaoColetaSangueVO> listarTransfusaoComExistsSemAgrupamentoComDoacao(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, Boolean indEstorno, 
			String ecoCsaCodigo, DominioOrigemAtendimento origem, String tdoCodigo, Boolean isTdoCodigoIgual);	
	
	public List<DoacaoColetaSangueVO> listarTransfusaoSemExistsSemAgrupamentoComDoacao(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, Boolean indEstorno, 
			String ecoCsaCodigo,  String tdoCodigo, Boolean isTdoCodigoIgual);
		
	public List<DoacaoColetaSangueVO> listarMovimentosComponentesSemExistsComAgrupamentoFiltrado(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo, Boolean indFiltrado);
	
	public List<DoacaoColetaSangueVO> listarMovimentosComponentesSemExistsComAgrupamentoDataFiltrado(Date vDtHrInicio, Date vDtHrFim,
			DominioMcoType mcoType, Boolean indEstorno, String ecoCsaCodigo, Boolean indFiltrado);
	
	public List<AbsExameComponenteVisualPrescricao> buscarExamesComponenteVisualPrescricao(
			String codigoComponenteSanguineo,
			String codigoProcedimentoHemoterapico) ;
	
	public AbsComponenteSanguineo obterComponenteSanguineosPorCodigo(
			String componenteSanguineoCodigo);
	
	public List<AbsSolicitacoesHemoterapicas> buscarSolicitacoesHemoterapicasPendentes(
			MpmPrescricaoMedica prescricao, Date data);
	
	public void inserirAbsAmostrasPacientes(AbsAmostrasPacientes absAmostrasPacientes, boolean flush);	
	
	public List<AbsSolicitacoesHemoterapicas> obterPrescricoesHemoterapicaPai(
			Integer seq, Integer seqAtendimento, Date dataHoraInicio,
			Date dataHoraFim);	
	
	public List<AbsSolicitacoesHemoterapicas> obterSolicitacoesHemoterapicasParaSumarioPrescricao(
			Integer pmeAtdSeq, Date dataInicioPrescricao, Date dataFimPrescricao);
	
	public void excluirAbsExameComponenteVisualPrescricao(Integer seqExameComponenteVisualPrescricao);

	public void manterAbsExameComponenteVisualPrescricao(
			AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao) throws ApplicationBusinessException ;

	
	public void atualizarAbsExameComponenteVisualPrescricao(
			AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao) throws ApplicationBusinessException ;
	
	public List<AbsItensSolHemoterapicas> buscaItensSolHemoterapicas(Integer pAtdSeq, Integer curPdtSeq);
	
	public AbsComponentePesoFornecedor obterAbsComponentePesoFornecedorPorChavePrimaria(AbsComponentePesoFornecedorId id, Boolean left, Enum... enumFetch);

	public void atualizarAbsComponentePesoFornecedor(AbsComponentePesoFornecedor elemento) throws ApplicationBusinessException;
	
	public void excluirAbsComponentePesoFornecedor(AbsComponentePesoFornecedorId id) throws ApplicationBusinessException;
	
	public void inserirAbsComponentePesoFornecedor(AbsComponentePesoFornecedor elemento) throws ApplicationBusinessException;

	public void alterarIndSugestao(AbsComponentePesoFornecedor absComponentePesoFornecedor) throws ApplicationBusinessException;

	AbsValidAmostrasComponentes obterValidadeAmostraPorCodigo(AbsValidAmostrasComponenteId id);

	List<AbsValidAmostrasComponentes> obterListaValidadeAmostrasPorCodigo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, String codComponente, Integer codigo, Integer idadeIni, Integer idadeFim);

	Long obterValidadeAmostrasPorCodigoCount(String codComponente, Integer codigo, Integer idadeIni, Integer idadeFim);

	void persistOrUpdate(AbsValidAmostrasComponentes validadeAmostra) throws BaseException;

	public List<AbsCandidatosDoadores> obterCandidatosDoadoresList(String parametro);

	public List<LinhaReportVO> pesquisarComponentesSanguineosAgendas(
			Integer agdSeq);

	void inserirFatProcedHospInternos(final ScoMaterial matCodigo,
								      final MbcProcedimentoCirurgicos pciSeq, 
								      final MpmProcedEspecialDiversos pedSeq,
								      final String csaCodigo, 
								      final String pheCodigo, 
								      final String descricao,
								      final DominioSituacao indSituacao, 
								      final Short euuSeq, 
								      final Integer cduSeq,
								      final Short cuiSeq, 
								      final Integer tidSeq)  throws ApplicationBusinessException;

	List<AbsComponenteSanguineo> listarAbsComponenteSanguineosAtivos(
			Object objPesquisa);

	Long listarAbsComponenteSanguineosAtivosCount(Object objPesquisa);

	public List<AbsComponenteSanguineo> listarAbsComponenteSanguineosNaoExisteNaCirurgia(
			Object objPesquisa, Integer crgSeq);

	public Long listarAbsComponenteSanguineosNaoExisteNaCirurgiaCount(
			Object objPesquisa, Integer crgSeq);

	public AbsComponenteSanguineo obterAbsComponenteSanguineoOriginal(
			String codigo);

	public Boolean existeAmostraPendente(Integer seqAtendimento, Integer seqCirurgia,
			Integer validade);
	
	void inserirSolicitacaoPorAmostra(AbsSolicitacoesPorAmostra solicitacaoPorAmostra) throws BaseException;
	
	List<AbsItensSolHemoterapicas> pesquisarAbsItensSolHemoterapicasPorAtdSeqSheSeq(Integer sheAtdSeq, Integer sheSeq);


	AbsGrupoJustificativaComponenteSanguineo obterGrupoJustificativaPorId(
			Short seq);
	
	public Boolean existeAbsComponenteSanguineo(String codigo);
}