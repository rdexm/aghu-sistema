package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;
import br.gov.mec.aghu.dominio.DominioIdentificacaoComponenteNPT;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescritoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.dominio.DominioTipoUsoDispensacao;
import br.gov.mec.aghu.exames.vo.UnitarizacaoVO;
import br.gov.mec.aghu.farmacia.vo.CadastroDiluentesJnVO;
import br.gov.mec.aghu.farmacia.vo.CadastroDiluentesVO;
import br.gov.mec.aghu.farmacia.vo.ComposicaoItemPreparoVO;
import br.gov.mec.aghu.farmacia.vo.DiluentesVO;
import br.gov.mec.aghu.farmacia.vo.HistoricoCadastroMedicamentoVO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoDispensadoPorBoxVO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoEstornadoMotivoVO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoPrescritoPacienteVO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoPrescritoUnidadeVO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoSinteticoVO;
import br.gov.mec.aghu.farmacia.vo.MedicoResponsavelVO;
import br.gov.mec.aghu.farmacia.vo.PrescricaoUnidadeVO;
import br.gov.mec.aghu.farmacia.vo.QuantidadePrescricoesDispensacaoVO;
import br.gov.mec.aghu.farmacia.vo.ViaAdministracaoVO;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.internacao.vo.MedicamentoVO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaFormaDosagemJn;
import br.gov.mec.aghu.model.AfaGrupoApresMdtos;
import br.gov.mec.aghu.model.AfaGrupoComponNptJn;
import br.gov.mec.aghu.model.AfaGrupoComponenteNpt;
import br.gov.mec.aghu.model.AfaGrupoMedicamento;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AfaItemPreparoMdto;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtosJn;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalenteJn;
import br.gov.mec.aghu.model.AfaMedicamentoJn;
import br.gov.mec.aghu.model.AfaMensagemMedicamento;
import br.gov.mec.aghu.model.AfaPrescricaoMedicamento;
import br.gov.mec.aghu.model.AfaSinonimoMedicamentoJn;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdmUnf;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoId;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoJN;
import br.gov.mec.aghu.model.AfaVinculoDiluentes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmUnidadeTempo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.model.VMpmMdtosDescr;
import br.gov.mec.aghu.prescricaomedica.vo.AfaViaAdmUnfVO;
import br.gov.mec.aghu.prescricaomedica.vo.ComponenteNPTVO;
import br.gov.mec.aghu.sig.custos.vo.SigPreparoMdtoPrescricaoMedicaVO;
import br.gov.mec.aghu.sig.custos.vo.SigPreparoMdtoVO;
import br.gov.mec.aghu.view.VAfaDescrMdto;
import br.gov.mec.aghu.vo.MpmPrescricaoMedicaVO;
import br.gov.mec.aghu.vo.PacientesEmAtendimentoVO;

public interface IFarmaciaFacade extends Serializable {

	public abstract void persistirAfaTipoVelocAdministracoes(
			AfaTipoVelocAdministracoes tipoVelocidadeAdministracao)
			throws BaseException;

	public abstract AfaViaAdministracao obterViaAdministracao(
			String siglaViaAdministracao);

	public abstract AfaMedicamento obterMedicamento(Integer codigoMedicamento);
	
	public void verificarVinculoDiluenteExistente(AfaMedicamento medicamento, Boolean indDiluente, DominioSituacaoMedicamento situacao ) throws ApplicationBusinessException;
	
	public abstract AfaVinculoDiluentes obterDiluente(Integer codigoDiluente);
	
	public abstract DiluentesVO buscarUsualPrescricaoPorMedicamento(Integer codigoMedicamento);

	public abstract AfaFormaDosagem buscarDosagenPadraoMedicamento(
			Integer medMatCodigo);

	public abstract AfaViaAdministracaoMedicamento buscarAfaViaAdministracaoMedimanetoPorChavePrimaria(
			AfaViaAdministracaoMedicamentoId chave);

	public abstract List<VAfaDescrMdto> obtemListaDiluentes();

	public abstract List<VAfaDescrMdto> obtemListaDiluentes(Integer matCodigo);
	
	public List<DiluentesVO> recuperaListaVinculoDiluente(String descricaoMedicamento, AfaMedicamento medicamento);
	
	public List<DiluentesVO> populaVoDiluentesVAfaDescrMdto(String descricaoMedicamento);

	public abstract List<AfaTipoVelocAdministracoes> obtemListaTiposVelocidadeAdministracao();

	public abstract List<AfaViaAdministracao> listarViasMedicamento(
			String strPesquisa, List<Integer> medMatCodigo,
			Short seqUnidadeFuncional);

	public abstract Long listarViasMedicamentoCount(String strPesquisa,
			List<Integer> medMatCodigo, Short seqUnidadeFuncional);

	public abstract String obterInfromacoesFarmacologicas(
			AfaMedicamento medicamento);

	List<br.gov.mec.aghu.farmacia.vo.MedicamentoVO> pesquisarMedicamentoAtivoPorCodigoOuDescricao(String parametro);
	
	public abstract List<AfaViaAdministracao> listarTodasAsVias(
			String strPesquisa, Short unfSeq);

	public abstract Long listarTodasAsViasCount(String strPesquisa,
			Short unfSeq);

	public abstract Boolean verificarViaAssociadaAoMedicamento(
			Integer medMatCodigo, String sigla);

	public abstract Boolean verificarBombaInfusaoDefaultViaAssociadaAoMedicamento(
			Integer medMatCodigo, String sigla);

	public abstract String obtemTipoUsoMedicamentoComDuracSol(Integer matCodigo);

	public abstract boolean isTipoVelocidadeAtiva(Short seq)
			throws ApplicationBusinessException;

	public abstract List<MedicamentoVO> obterMedicamentosVO(String strPesquisa,
			Boolean listaMedicamentos, DominioSituacaoMedicamento[] situacoes, Boolean somenteMdtoDeUsoAmbulatorial , Boolean listaMedicamentosAux);

	public abstract List<MedicamentoVO> obterMedicamentosModeloBasicoVO(
			Object strPesquisa);

	public abstract Long obterMedicamentosVOCount(String strPesquisa,
			Boolean listaMedicamentos, DominioSituacaoMedicamento[] situacoes,
			Boolean somenteMdtoDeUsoAmbulatorial, Boolean listaMedicamentosAux);

	List<ViaAdministracaoVO> pesquisarViaAdminstracaoSiglaouDescricao(String param);

	br.gov.mec.aghu.farmacia.vo.MedicamentoVO buscarMedicamentoPorCodigo(Integer matCodigo);

	List<br.gov.mec.aghu.farmacia.vo.MedicamentoVO> pesquisarMedicamentoPorCodigosEmergencia(List<Integer> matCodigos);
	
	public abstract Long obterMedicamentosModeloBasicoVOCount(
			Object strPesquisa);

	public abstract List<AfaMedicamento> obterMedicamentos(Object strPesquisa,
			Boolean listaMedicamentos);

	public abstract Integer obterMedicamentosCount(Object strPesquisa,
			Boolean listaMedicamentos);

	public abstract DominioSituacao obtemSituacaoFormaDosagem(Integer seq,
			Integer medMatCodigo);

	public abstract Long listaTipoUsoMedicamentoPorMedicamentoEGupSeqCount(
			Integer codigoMedicamento, Integer gupSeq);

	/**
	 * Busca Todas as vias sem filtro por unidade de internação
	 * 
	 * @param strPesquisa
	 * @return
	 */
	public abstract List<AfaViaAdministracao> getListaTodasAsVias(
			String strPesquisa);

	/**
	 * Busca as vias de administração não utilizadas em uma forma de dosagem
	 * 
	 * @param strPesquisa
	 * @return
	 */
	public abstract List<AfaViaAdministracao> getListaViasAdmNaoUtilizadas(
			String strPesquisa, Set<AfaViaAdministracaoMedicamento> listaViasAdm);

	/**
	 * Lista somente as vias cadastradas para os medicamentos, conforme seus
	 * códigos e SEM utilizar o código da unidade de internação do paciente.
	 * 
	 * @param strPesquisa
	 * @param listaDeIds
	 * @return
	 */
	public abstract List<AfaViaAdministracao> getListaViasMedicamento(
			String strPesquisa, List<Integer> listaDeIds);

	public abstract Long getListaTodasAsViasCount(String strPesquisa);

	public abstract Long getListaViasMedicamentoCount(String strPesquisa,
			List<Integer> listaDeIds);

	public abstract List<VAfaDescrMdto> obterMedicamentosReceitaVO(
			Object strPesquisa);

	public abstract Long obterMedicamentosReceitaVOCount(Object strPesquisa);

	public abstract AfaMensagemMedicamento obterAfaMensagemMedicamento(
			Integer seqMensagemMedicamento);

	public abstract void removerAfaMensagemMedicamento(Integer mensagemMedicamentoSeq) throws ApplicationBusinessException;

	public abstract Long pesquisaAfaMensagemMedicamentoCount(
			Integer filtroSeq, String filtroDescricao,
			Boolean filtroCoexistente, DominioSituacao filtroSituacao);

	public abstract List<AfaMensagemMedicamento> pesquisaAfaMensagemMedicamento(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer filtroSeq, String filtroDescricao,
			Boolean filtroCoexistente, DominioSituacao filtroSituacao);

	public abstract AfaTipoVelocAdministracoes obterAfaTipoVelocAdministracoes(
			Short seqVelocidadeAdministracao);

	public abstract void removerAfaTipoVelocAdministracoes(Short seq) throws BaseException;

	public abstract Long pesquisaAfaVelocidadesAdministracaoCount(
			Short filtroSeq, String filtroDescricao,
			BigDecimal filtroFatorConversaoMlH, Boolean filtroTipoUsualNpt,
			Boolean filtroTipoUsualSoroterapia, DominioSituacao filtroSituacao);

	public abstract List<AfaTipoVelocAdministracoes> pesquisaAfaVelocidadesAdministracao(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short filtroSeq, String filtroDescricao,
			BigDecimal filtroFatorConversaoMlH, Boolean filtroTipoUsualNpt,
			Boolean filtroTipoUsualSoroterapia, DominioSituacao filtroSituacao);

	public abstract AfaGrupoMedicamento obterAfaGrupoMedicamento(
			Short seqGrupoMedicamento);

	public abstract void removerAfaGrupoMedicamento(Short seqAfaGrupoMedicamento) throws BaseException;

	public abstract Long pesquisaAfaGrupoMedicamentoCount(Short filtroSeq,
			String filtroDescricao, Boolean filtroMedicamentoMesmoSal,
			Boolean filtroUsoFichaAnestesia, DominioSituacao filtroSituacao);

	public abstract List<AfaGrupoMedicamento> pesquisaAfaGrupoMedicamento(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short filtroSeq, String filtroDescricao,
			Boolean filtroMedicamentoMesmoSal, Boolean filtroUsoFichaAnestesia,
			DominioSituacao filtroSituacao);

	public abstract void persistirAfaMensagemMedicamento(
			AfaMensagemMedicamento mensagemMedicamento) throws BaseException;
	
	public abstract void efetuarInclusaoDiluente(AfaVinculoDiluentes diluente, RapServidores servidorLogado, VAfaDescrMdto diluenteSelecionado, Integer codigoMedicamentoSelecionado) throws BaseException;
	
	public abstract void efetuarAlteracaoDiluente(AfaVinculoDiluentes diluente, RapServidores servidorLogado, VAfaDescrMdto diluenteSelecionado, Integer codigoMedicamentoSelecionado) throws BaseException;
	
	public abstract void efetuarRemocao(AfaVinculoDiluentes diluente, RapServidores servidorLogado) throws BaseException;

	public abstract List<AfaGrupoMedicamento> pesquisaGruposMedicamentos(
			Object filtro);

	public abstract Long pesquisaGruposMedicamentosCount(String filtro);

	public abstract void persistirAfaGrupoMedicamento(
			AfaGrupoMedicamento grupoMedicamento) throws BaseException;

	public abstract List<AfaGrupoUsoMedicamento> obterTodosGruposUsoMedicamento();

	public abstract List<AfaTipoOcorDispensacao> pesquisarOcorrencias(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short seqOcorrencia, String descricaoOcorrencia,
			DominioTipoUsoDispensacao tipoUsoOcorrencia,
			DominioSituacao situacaoPesq);

	public abstract Long pesquisarOcorrenciasCount(Short seqOcorrencia,
			String descricaoOcorrencia,
			DominioTipoUsoDispensacao tipoUsoOcorrencia,
			DominioSituacao situacaoPesq);

	public abstract AfaTipoOcorDispensacao obterOcorrencia(Short seq);

	public abstract List<AfaMedicamento> pesquisarListaMedicamentos(
			Object strPesquisa);

	public abstract Long pesquisarListaMedicamentosCount(Object strPesquisa);

	public abstract List<AfaGrupoUsoMedicamento> pesquisarTodosGrupoUsoMedicamento(
			Object strPesquisa);

	public abstract Integer pesquisarTodosGrupoUsoMedicamentoCount(
			Object strPesquisa);

	public abstract List<AfaTipoUsoMdto> pesquisarTodosTipoUsoMedicamento(
			Object strPesquisa);

	public abstract Integer pesquisarTodosTipoUsoMedicamentoCount(
			Object strPesquisa);

	public abstract void verificarDelecao(Date data)
			throws ApplicationBusinessException;

	public abstract List<PrescricaoUnidadeVO> obterConteudoRelatorioPrescricaoPorUnidade(
			AghUnidadesFuncionais unidadeFuncional, Date dataDeReferencia,
			String validade, Boolean indPmNaoEletronica) throws ApplicationBusinessException;

	public abstract List<MedicamentoPrescritoUnidadeVO> obterConteudoRelatorioMedicamentoPrescritoPorUnidade(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim,
			AghUnidadesFuncionais unidadeFuncional, AfaMedicamento medicamento,
			Boolean itemDispensado) throws ApplicationBusinessException,
			ApplicationBusinessException;

	public abstract List<MedicamentoPrescritoPacienteVO> obterConteudoRelatorioMedicamentosPrescritosPorPaciente(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim,
			AghUnidadesFuncionais unidadeFuncional, AfaMedicamento medicamento,
			AfaGrupoUsoMedicamento grupo, AfaTipoUsoMdto tipo,
			Boolean itemDispensado, Integer pacCodigo) throws ApplicationBusinessException;

	public abstract List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
			Object parametro);

	public abstract List<AfaLocalDispensacaoMdtos> recuperarListaPaginadaLocalDispensacaoMdtos(
			AfaMedicamento medicamento, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);
	
	public abstract List<CadastroDiluentesVO> recuperarListaPaginadaDiluentes(
			Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, AfaMedicamento medicamento);

	public abstract Long pesquisarLocalDispensacaoMedicamentoCount(
			AfaMedicamento medicamento);
	
	public Long pesquisarDiluentesCount(AfaMedicamento medicamento);

	/*public abstract Long pesquisarDispensacaoMdtosListaPaginada(
			MpmPrescricaoMedica prescricaoMedica, Short unfSeq);*/

	public abstract List<AghUnidadesFuncionais> listarUnidadesPmeInformatizadaByPesquisa(
			Object parametro);

	public abstract List<AghUnidadesFuncionais> listarUnidadesPrescricaoByPesquisa(
			Object parametro);
	
	public abstract Long pesquisarItensDispensacaoMdtosCount(
			AghUnidadesFuncionais unidadeSolicitante, Integer prontuario,
			String nomePaciente, Date dtHrInclusaoItem,
			AfaMedicamento medicamento,
			DominioSituacaoDispensacaoMdto situacao,
			AghUnidadesFuncionais farmacia, AghAtendimentos aghAtendimentos,
			MpmPrescricaoMedica prescricaoMedica, String loteCodigo, Boolean indPmNaoEletronica);
			
	public abstract List<AfaMedicamento> pesquisarMdtosParaDispensacaoPorItemPrescrito(
			MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica);

	public abstract List<AfaTipoOcorDispensacao> pesquisarTipoOcorrenciasAtivasEstornadas(Short ... seqsNotIn);

	public abstract List<MedicamentoEstornadoMotivoVO> obterConteudoRelatorioMedicamentoEstornadoPorMotivo(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim,
			AghUnidadesFuncionais unidadeFuncional,
			AfaTipoOcorDispensacao tipoOcorDispensacao,
			AfaMedicamento medicamento) throws ApplicationBusinessException,
			ApplicationBusinessException;

	public abstract List<AfaTipoOcorDispensacao> pesquisarMotivoOcorrenciaDispensacao(
			Object strPesquisa);

	public abstract List<AfaMedicamento> pesquisarMedicamentosPorCodigoDescricao(
			String parametro);

	public abstract Long pesquisarMedicamentosCountPorCodigoDescricao(
			String parametro);

	public abstract String gerarEtiquetas(SceLoteDocImpressao entidade)
			throws ApplicationBusinessException;

	public abstract List<AfaFormaDosagemJn> pesquisarFormaDosagemJn(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AfaMedicamento medicamento);
	
	public abstract List<CadastroDiluentesJnVO> pesquisarVinculoDiluentesJn(Integer firstResult, Integer maxResult, String orderProperty,boolean asc, Integer codigoMedicamento);

	public abstract Long pesquisarFormaDosagemJnCount(
			AfaMedicamento medicamento);
	
	public abstract Long pesquisarVinculoDiluentesJnCount(Integer medicamento);

	public abstract AfaFormaDosagem obterAfaFormaDosagem(Integer seq);

	public abstract void atualizaAfaDispMdto(AfaDispensacaoMdtos admNew,
			AfaDispensacaoMdtos admOld, String nomeMicrocomputador) throws BaseException;

	public abstract void criaDispMdtoTriagemPrescricao(
			AfaDispensacaoMdtos admNew, String nomeMicrocomputador) throws ApplicationBusinessException;

	public abstract AfaDispensacaoMdtos getAfaDispOldDesatachado(
			AfaDispensacaoMdtos adm) throws ApplicationBusinessException;

	public abstract String atribuirValidadeDaPrescricaoMedica(
			Date dataDeReferencia, AghUnidadesFuncionais unidadeFuncional);

	public abstract List<AfaMedicamentoEquivalenteJn> pesquisarMedicamentoEquivalenteJn(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AfaMedicamento medicamento);

	public abstract Long pesquisarMedicamentoEquivalenteJnCount(
			AfaMedicamento medicamento);

	public abstract List<AfaLocalDispensacaoMdtosJn> recuperarListaPaginadaLocalDispensacaoMdtosJn(
			AfaMedicamento medicamento, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	public abstract List<QuantidadePrescricoesDispensacaoVO> pesquisarRelatorioQuantidadePrescricoesDispensacao(
			Date dataEmissaoInicio, Date dataEmissaoFim)
			throws ApplicationBusinessException;

	public abstract List<AfaSinonimoMedicamentoJn> pesquisarSinonimoMedicamentoJn(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AfaMedicamento medicamento);

	public abstract Long pesquisarSinonimoMedicamentoJnCount(
			AfaMedicamento medicamento);

	public abstract Long pesquisarLocalDispensacaoMedicamentoCountJn(
			AfaMedicamento medicamento);

	public abstract List<AfaViaAdministracaoMedicamentoJN> pesquisarViasAdministracaoJn(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AfaMedicamento medicamento);

	public abstract Long pesquisarViasAdministracaoJnCount(
			AfaMedicamento medicamento);

	//Estória # 5697
	public abstract List<MedicamentoSinteticoVO> obterListaTodosMedicamentos()
			throws ApplicationBusinessException;

	// HISTORICO CADASTRO DE MEDICAMENTO
	public abstract List<AfaMedicamentoJn> pesquisarHistoricoCadastroMedicamento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AfaMedicamento medicamento);

	public abstract Long pesquisarHistoricoCadastroMedicamentoCount(
			AfaMedicamento medicamento);

	public abstract HistoricoCadastroMedicamentoVO obterHistoricoCadastroMedicamento(
			Integer seqJn) throws ApplicationBusinessException;

	///Estória # 5714
	public abstract List<MedicamentoDispensadoPorBoxVO> obterListaMedicamentosDispensadosPorBox(
			Date dataEmissaoInicio, Date dataEmissaoFim,
			AghMicrocomputador aghMicrocomputador, AfaMedicamento medicamento,
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento,
			AfaGrupoApresMdtos afaGrupoApresMdtos, Integer pacCodigo)
			throws ApplicationBusinessException;

	//#5714
	public abstract List<AfaTipoApresentacaoMedicamento> pesquisarTipoApresMdtos(
			Object strPesquisa);

	//#5714
	public abstract List<AfaGrupoApresMdtos> pesquisarGrupoApresMdtos(
			Object strPesquisa);

	//#5714
	public abstract Long pesquisarGrupoApresMdtosCount(Object strPesquisa);

	//Estória # 5714
	public abstract BigDecimal obterCustoMedioPonderado(
			Integer codigoScoMaterial, Date dataCompetencia)
			throws ApplicationBusinessException;

	public abstract void mpmpGeraDispTot(Integer pmeAtdSeq, Integer pmeSeq,
			Date dthrInicio, Date dthrFim, String nomeMicrocomputador)
			throws ApplicationBusinessException;

	public abstract void mpmpGeraDispMVto(Integer pmeAtdSeq, Integer pmeSeq,
			Date dthrInicioMvtoPendente, Date dthrInicio, Date dthrFim, String nomeMicrocomputador)
			throws BaseException, ApplicationBusinessException;

	public abstract TipoOperacaoEnum getMpmcOperMvto(Date pmdAlteradoEm,
			Integer pmdAtdSeq, Long pmdSeq);

	//Estória # 5388
	public abstract List<DominioSituacaoDispensacaoMdto> getDominioSituacaoDispensacaoMdto();

	public abstract List<AfaMedicamento> pesquisarMdtosParaDispensacaoPorItemPrescrito(
			MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica, Object strPesquisa);

	public abstract Long pesquisarMdtosParaDispensacaoPorItemPrescritoCount(
			MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica, Object strPesquisa);
	
	List<AfaMedicamento> pesquisarAfaMedicamentosAtivosExistentesEmMedicUsuais(final Short unfSeq, final String filtro, final boolean firstQuery);
	
	Long pesquisarAfaMedicamentosAtivosExistentesEmMedicUsuaisCount(final Short unfSeq, final String filtro, boolean firstQuery);
	
	/**
	 * Pesquisa Unidades funcionais por código/ descrição/ Ala e Andar, filtrando também por características específicas
	 * @param parametroPesquisa
	 * @return List<AghUnidadesFuncionais>
	 */
	public abstract List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicas(
			Object parametroPesquisa);

	public abstract Long pesquisarTipoApresMdtosCount(Object strPesquisa);

	public abstract Long pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicasCount(
			String strPesquisa);

	public abstract List<AghUnidadesFuncionais> listarFarmaciasAtivasByPesquisa(
			Object strPesquisa);

	public abstract Long listarFarmaciasAtivasByPesquisaCount(
			Object strPesquisa);

	public abstract AfaTipoApresentacaoMedicamento obterAfaTipoApresentacaoMedicamentoPorId(
			String tprSigla);

	public abstract AfaGrupoMedicamento obterAfaGrupoMedicamentoComItemGrupoMdto(
			Integer matCodigo, Boolean usoFichaAnestesia);
	
	public abstract List<MedicamentoVO> obterMedicamentosEnfermeiroObstetra(String strPesquisa,
			Boolean listaMedicamentos, DominioSituacaoMedicamento[] situacoes, Boolean somenteMdtoDeUsoAmbulatorial);		
	
	public abstract List<VAfaDescrMdto> obterMedicamentosEnfermeiroObstetraReceitaVO(Object strPesquisa);
	
	/**
	 * @author heliz
	 * #15822
	 * Métodos implementados para a #15822 - Visualizar sumário de transferência do paciente
	 * na internação
	 */
	//----

	public abstract String obterLocalizacaoPacienteParaRelatorio(
			AghAtendimentos atendimento);

	Long listarUnidadesPrescricaoByPesquisaCount(Object parametro);

	Boolean verificaExclusao(MpmUnidadeTempo unidadeTempo);
	
	Long listarUnidadesPmeInformatizadaByPesquisaCount(Object parametro);

	Long pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
			Object parametro);

	Long pesquisarMotivoOcorrenciaDispensacaoCount(Object strPesquisa);
		
	Boolean verificarSeMedicamentoPossuiViaSiglaDiferente(String sigla, Integer matCodigo);
	
	AfaGrupoUsoMedicamento obterAfaGrupoUsoMedicamentoPorChavePrimaria(Integer seq);
	
	AfaTipoVelocAdministracoes obterAfaTipoVelocAdministracoesDAO(Short seq);
	
	DominioSituacao obtemSituacaoTipoVelocidade(Short seq);
	
	List<AfaViaAdministracao> listarViasAdministracao(final Object param);
	
	Long listarViasAdministracaoCount(String param);
	
	Long pesquisarViasAdministracaoCount(String sigla, String descricao, DominioSituacao situacao);
	
	List<AfaViaAdministracao> pesquisarViasAdministracao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String sigla, String descricao,
			DominioSituacao situacao);
	
	void removerViaAdministracao(AfaViaAdministracao viaAdministracao);
	
	boolean existeSiglaCadastrada(String sigla);
	
	/**
	 * Verificar a existência de registros de tipo item de dieta em outras entidades
	 * @param tipoDieta
	 * @param class1
	 * @param field
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	boolean existeItemViaAdministracao(AfaViaAdministracao viaAdministracao, Class class1, Enum field);
	
	AfaViaAdministracao inserirViaAdministracao(AfaViaAdministracao viaAdministracao);

	AfaViaAdministracao atualizarViaAdministracao(AfaViaAdministracao viaAdministracao, boolean flush);
	
	AfaViaAdministracaoMedicamento obterViaAdministracaoMedicamento(AfaViaAdministracaoMedicamentoId id);

	/**
	 * Lista AfaViaAdmUnf ativas por unidade funcinal.
	 * 
	 * @param unidade
	 * @return
	 */
	List<AfaViaAdmUnf> listarAfaViaAdmUnfAtivasPorUnidadeFuncional(AghUnidadesFuncionais unidade);
	
	/**
	 * Lista AfaViaAdmUnf ativas por unidade funcinal e via de administração
	 * 
	 * @param unidade
	 * @param viaAdministracao
	 * @return
	 */
	List<AfaViaAdmUnf> listarAfaViaAdmUnfAtivasPorUnidadeFuncionalEViaAdministracao(AghUnidadesFuncionais unidade, AfaViaAdministracao viaAdministracao);
	
	AfaViaAdmUnf obterViaAdmUnfId(String sigla, Short unfSeq);

	List<AfaViaAdmUnf> listarViaAdmUnf(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AghUnidadesFuncionaisVO unidFuncionais);
	
	List<AfaViaAdmUnfVO> listarViaAdmUnfVO(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AghUnidadesFuncionais unidFuncionais);	

	Long listarViaAdmUnfCount(AghUnidadesFuncionaisVO unidFuncionais);
		
	public List<VAfaDescrMdto> obtemListaDescrMdtoAtivos(final Object parametro);
	
	public Long obtemListaDescrMdtoAtivosCount(final Object parametro);	
	
	/**
	 * Executa a seguinte consulta para verificar se existe dependentes em AfaTempoEstabilidadesJN.
	 * @param Object MpmUnidadeTempo
	 * @return Boolean
	 */
	public Boolean verificaExclusaoJN(MpmUnidadeTempo unidadeTempo);

	List<AfaMedicamento> pesquisarMedicamentosPorTipoApresentacao(String sigla);

	public List<Short> listarOrdemSumarioPrecricao(Integer ituSeq);
	
	public void gerarCSVInterfaceamentoImpressoraGriffols(UnitarizacaoVO unitarizacaoVo, Long quantidade) throws ApplicationBusinessException;

	public abstract List<AfaMedicamento> pesquisarMedicamentosAtivos(
			Object objPesquisa);

	public abstract Long pesquisarMedicamentosAtivosCount(Object objPesquisa);
	
	AfaDispensacaoMdtos obterDispensacaoMdtosPorItemPrescricaoMdtoQtdSolicitada(MpmItemPrescricaoMdtoId itemPrescricaoMdtoId);
	
	Boolean existeDispensacaoAnteriorPacienteUTI(Integer atdSeq, Short unfSeq);

	public List<AfaViaAdministracao> listarTodasAsVias(String strPesquisa);
	public List<AfaViaAdmUnf> listarAfaViaAdmUnfPorUnidadeFuncional(AghUnidadesFuncionais unidade);

	List<AfaMedicamento> pesquisarMedicamentosParaMedicamentosCuidados(String parametro);

	Long pesquisarMedicamentosParaMedicamentosCuidadosCount(String parametro);

	List<AfaMedicamento> pesquisarMedicamentosParaListagemMedicamentosCuidados(Integer matCodigo, DominioSituacaoMedicamento indSituacao, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);

	Long pesquisarMedicamentosParaListagemMedicamentosCuidadosCount(Integer matCodigo, DominioSituacaoMedicamento indSituacao);

	AfaMedicamento obterMedicamentoPorMatCodigo(Integer matCodigo);

	public abstract AfaMedicamento obterMedicamentoOriginal(Integer matCodigo);
	
	public List<SigPreparoMdtoVO> buscarBolsasSeringasDinpensacoes(Date dataInicioProcessamento, 
			Date dataFimProcessamento, 
			Integer tipoTratamento/*"P_TIPO_TRAT_QUIMIO"*/);

	List<AfaViaAdministracaoMedicamento> pesquisarAfaViaAdministracaoMedicamento(
			AfaMedicamento medicamento);

	public abstract AfaMedicamento obterMedicamentoComUnidadeMedidaMedica(
			Integer codigo);

	public abstract AfaGrupoMedicamento obterAfaGrupoMedicamentoComItemGrupoMdto(
			Short seqAfaGrupoMedicamento);

	/**
	 * Listar os medicamentos ativos
	 * 
	 * Web Service #36672
	 * 
	 * @param parametro
	 * @param maxResults
	 * @return
	 */
	List<AfaMedicamento> pesquisarMedicamentoAtivoPorCodigoOuDescricao(String parametro, Integer maxResults);

	/**
	 * Count de medicamentos ativos
	 * 
	 * Web Service #36672
	 * 
	 * @param parametro
	 * @return
	 */
	Long pesquisarMedicamentoAtivoPorCodigoOuDescricaoCount(String parametro);
	
		
	List<MpmPrescricaoMedicaVO> listarPrescricaoMedicaSituacaoDispensacaoUnion2(
			String leitoId, Integer prontuario, String nome, Date dtHrInicio,
			Date dtHrFim, String seqPrescricao, Boolean indPacAtendimento);
	

	/**
	 * Listar os medicamentos por código
	 * 
	 * Web Service #36675
	 * 
	 * @param matCodigos
	 * @return
	 */
	List<AfaMedicamento> pesquisarMedicamentoPorCodigos(List<Integer> matCodigos);
	
	void gerarCSVInterfaceamentoImpressoraHujf(UnitarizacaoVO unitarizacaoVo, Long quantidade) throws ApplicationBusinessException; 
	
//	public void imprimeEtiquetaExtrasOuInterfaceamentoUnitarizacao(SceLoteDocImpressao loteDocImpressao, 
//			RapServidores servidorLogado,String nomeMicrocomputador, Integer qtdeEtiquetas) 
//				throws BaseException;
	
	public abstract void excluir(AfaViaAdmUnfVO viaVinculadoUnidade,
			RapServidores servidor) throws ApplicationBusinessException;

	public abstract void incluirTodasViasUnf(
			AghUnidadesFuncionais unidFuncionais, RapServidores servidor)
			throws ApplicationBusinessException;

	public abstract void incluirViasUnfs(AghUnidadesFuncionais unidFuncionais,
			RapServidores servidor) throws ApplicationBusinessException;
	
	
	List<ComposicaoItemPreparoVO> pesquisarComposicaoItemPreparo(Integer itoPtoSeq, Short itoSeqp);
	
	List<SigPreparoMdtoPrescricaoMedicaVO> buscarBolsasSeringasDinpensacoesPrescricaoMedica(Date dataInicio, Date dataFim);
	
	public abstract List<AghUnidadesFuncionais> pesquisarPorDescricaoCodigoAtivaAssociada(
			String string);

	public abstract Long pesquisarPorDescricaoCodigoAtivaAssociadaCount(
			String string);

	public List<MedicoResponsavelVO> pesquisarMedicoResponsavel(
			String strPesquisa,Integer matriculaResponsavel, Short vinCodigoResponsavel);

	public abstract List<PacientesEmAtendimentoVO> listarPacientesEmAtendimento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer pacCodigo, String leito, Short unfSeq,
			Integer matriculaResp, Short vinCodigoResp,
			DominioOrigemAtendimento origem, Short espSeq) throws ApplicationBusinessException;

	public abstract Long listarPacientesEmAtendimentoCount(
			Integer pacCodigo, String leito, Short unfSeq,
			Integer matriculaResp, Short vinCodigoResp,
			DominioOrigemAtendimento origem, Short espSeq) throws ApplicationBusinessException ;
	
	public AfaPrescricaoMedicamento obterAfaPrescricaoMedicamento(
			Long seqAfaPrescricaoMedicamento);

	public AfaDispensacaoMdtos processaNovaAfaDispMdto(Integer atdSeq, Integer pmeSeq,
			Integer pmdAtdSeq, Long pmdSeq, Integer medMatCodigo,
			Short imeSeqp, Boolean prescricaoMedicamentoIndSeNecessario,
			BigDecimal dose, BigDecimal percSeNecessario, Integer fdsSeq,
			DominioSituacaoItemPrescritoDispensacaoMdto indSitItemPrescrito,
			Long dsmSequencia, Boolean indPmNaoEletronica, Long prmSeq)
			throws ApplicationBusinessException;
	
	public Long pesquisarMedicamentosCount(Object objPesquisa,
			DominioSituacao situacaoMedicamento,
			Boolean joinTipoApresentacaoMedicamento,
			DominioSituacao situacaoTipoApresentacaoMedicamento);

	public List<AfaMedicamento> pesquisarMedicamentos(Object objPesquisa,
			DominioSituacao situacaoMedicamento,
			Boolean joinTipoApresentacaoMedicamento,
			DominioSituacao situacaoTipoApresentacaoMedicamento, String ... orders);
	
	public void processaDispensacaoDiluente(MpmPrescricaoMdto prescricaoMdto, 
			BigDecimal percSeNec, Date pmeData, Date pmeDthrFim, String nomeMicrocomputador, 
			Boolean movimentacao
			) throws ApplicationBusinessException;
	public AfaItemPreparoMdto obterItemPreparoMdtosPorChavePrimaria(Short seqp, Integer ptoSeq);
	
	List<ComponenteNPTVO> pesquisarHistoricoComponenteNPT(Integer seqComponente);
	
	List<AfaGrupoComponNptJn> pesquisarHistoricoGrupoComponenteNPT(Short seq);
	
	List<ComponenteNPTVO> pesquisarComponentesNPT(VMpmMdtosDescr medicamento,AfaGrupoComponenteNpt grupo,
			String descricao,DominioSimNao situacao,DominioSimNao adulto,DominioSimNao pediatria,
			Short ordem,DominioIdentificacaoComponenteNPT identificacao,Integer firstResult, 
			Integer maxResult,String orderProperty, boolean asc);
	
	Long pesquisarComponentesNPTCount(VMpmMdtosDescr medicamento,AfaGrupoComponenteNpt grupo,
			String descricao,DominioSimNao situacao,DominioSimNao adulto,DominioSimNao pediatria,
			Short ordem,DominioIdentificacaoComponenteNPT identificacao);
	
	/**
	 * Retorna a unidade funcional associado ao computador.
	 * 
	 * @param computador
	 *            nome ou ip do computador
	 * @return
	 * @throws ApplicationBusinessException
	 *             COMPUTADOR_SEM_UNIDADE_FUNCIONAL
	 *             AGH_MICROCOMPUTADOR_NAO_CADASTRADO
	 */
	AghUnidadesFuncionais getUnidadeFuncionalAssociada(String computador)
			throws ApplicationBusinessException;

	public abstract List<AfaMedicamento> obterMedicamentosParaInclusaoLocalDispensacao(List<Integer> listaMateriais);

	AfaPrescricaoMedicamento obterAfaPrescricaoMedicamento(Long seqAfaPrescricaoMedicamento, Boolean left, Enum... fetchArgs);
	
	
}