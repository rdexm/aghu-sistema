package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.vo.CadastroMateriaisImpressaoNotaSalaVO;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.vo.RelatorioEscalaProfissionaisSemanaVO;
import br.gov.mec.aghu.blococirurgico.vo.HistoricoAlteracoesGrupoAlcadaVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcpProcedimentoCirurgicoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioResumoCirurgiasRealizadasPorPeriodoListVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioResumoCirurgiasRealizadasPorPeriodoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.dominio.DominioTipoConvenioOpms;
import br.gov.mec.aghu.dominio.DominioTipoSala;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.MbcAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcAreaTricProcCirg;
import br.gov.mec.aghu.model.MbcAreaTricProcCirgId;
import br.gov.mec.aghu.model.MbcAreaTricotomia;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaractSalaEspId;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCidUsualEquipe;
import br.gov.mec.aghu.model.MbcCompSangProcCirg;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcDestinoPaciente;
import br.gov.mec.aghu.model.MbcEquipamentoCirgPorUnid;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.MbcEquipamentoNotaSala;
import br.gov.mec.aghu.model.MbcEquipamentoNotaSalaId;
import br.gov.mec.aghu.model.MbcEscalaProfUnidCirg;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgsId;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcGrupoProcedCirurgico;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirgId;
import br.gov.mec.aghu.model.MbcMaterialImpNotaSalaUn;
import br.gov.mec.aghu.model.MbcMotivoAtraso;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.model.MbcMotivoDemoraSalaRec;
import br.gov.mec.aghu.model.MbcMvtoCaractSalaCirg;
import br.gov.mec.aghu.model.MbcMvtoSalaCirurgica;
import br.gov.mec.aghu.model.MbcMvtoSalaEspEquipe;
import br.gov.mec.aghu.model.MbcNecessidadeCirurgica;
import br.gov.mec.aghu.model.MbcPerfilCancelamento;
import br.gov.mec.aghu.model.MbcProcPorEquipe;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProcedimentoPorGrupo;
import br.gov.mec.aghu.model.MbcProcedimentoPorGrupoId;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcQuestao;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcServidorAvalOpms;
import br.gov.mec.aghu.model.MbcSinonimoProcCirg;
import br.gov.mec.aghu.model.MbcSinonimoProcCirgId;
import br.gov.mec.aghu.model.MbcTipoAnestesiaCombinada;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.model.MbcTipoSala;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.model.MbcUnidadeNotaSala;
import br.gov.mec.aghu.model.MbcUnidadeNotaSalaId;
import br.gov.mec.aghu.model.MbcValorValidoCanc;
import br.gov.mec.aghu.model.PdtDescPadrao;
import br.gov.mec.aghu.model.PdtEquipPorProc;
import br.gov.mec.aghu.model.PdtEquipamento;
import br.gov.mec.aghu.model.PdtInstrPorEquip;
import br.gov.mec.aghu.model.PdtInstrPorEquipId;
import br.gov.mec.aghu.model.PdtInstrumental;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.VMbcProfServidor;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;


public interface IBlocoCirurgicoCadastroApoioFacade extends Serializable {

	List<AghUnidadesFuncionais> buscarUnidadesFuncionaisCirurgia(Object objPesquisa);

	Long contarUnidadesFuncionaisCirurgia(Object objPesquisa);

	List<MbcHorarioTurnoCirg> buscarMbcHorarioTurnoCirg(MbcHorarioTurnoCirg mbcHorarioTurnoCirg);

	void excluirMbcHorarioTurnoCirg(MbcHorarioTurnoCirgId id) throws BaseException;

	void persistirMbcHorarioTurnoCirg(MbcHorarioTurnoCirg mbcHorarioTurnoCirg, String nomeMicrocomputador) throws BaseException;

	MbcHorarioTurnoCirg obterMbcHorarioTurnoCirgPorId(MbcHorarioTurnoCirgId id);
	
	public void inserirCidUsualEquipe(MbcCidUsualEquipe elemento) throws BaseException;
	
	public void atualizarCidUsualEquipe(MbcCidUsualEquipe elemento) throws BaseException;

	public List<MbcUnidadeNotaSala> buscarNotasSalaPorUnidadeCirurgica(final Short unfSeq);
	
	void excluirNotaDeSala(MbcUnidadeNotaSalaId mbcUnidadeNotaSalaId) throws ApplicationBusinessException;

	void persistirNotaDeSala(MbcUnidadeNotaSala mbcUnidadeNotaSala) throws ApplicationBusinessException;
	
	void persistirEquipamentoNotaDeSala(MbcEquipamentoNotaSala mbcEquipamentoNotaSala) throws ApplicationBusinessException;
	
	void excluirEquipamentoNotaDeSala(MbcEquipamentoNotaSalaId equipamentoNotaSalaId);	
	
    Long pesquisarProcedimentosCirurgicosPorCodigoDescricaoCount(Object filtro, DominioSituacao situacao);
    
    List<MbcProcedimentoCirurgicos> pesquisarProcedimentosCirurgicosPorCodigoDescricao(Object filtro, String order, Integer maxResults, DominioSituacao situacao);
	
	List<MbcProcedimentoCirurgicos> pesquisaProcedimentoCirurgicosPorCodigoDescricaoSituacao(Object filtro, DominioSituacao situacao, String order);
	
	List<MbcProcPorEquipe> pesquisarProcedimentosUsadosEquipe(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MbcProcPorEquipe elemento);
	
	List<MbcNecessidadeCirurgica> listarNecessidadesFiltro(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc, 
			final Short necesSeq, final String descricaoNecess, final AghUnidadesFuncionais unidFunc, final DominioSituacao situacao, final Boolean requerDescricao);
	
	Long listarNecessidadesFiltroCount(final Short necesSeq, final String descricaoNecess, final AghUnidadesFuncionais unidFunc, final DominioSituacao situacao, final Boolean requerDescricao);
	
	
	List<MbcProfAtuaUnidCirgs> listarProfissionaisUnidCirFiltro(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final AghUnidadesFuncionais unidadeFuncional, final Integer matricula, final Short vinCodigo, final String nome, final DominioFuncaoProfissional funcaoProfiss, final DominioSituacao situacao);

	Long listarProfissionaisUnidCirFiltroCount(final AghUnidadesFuncionais unidadeFuncional, final Integer matricula, final Short vinCodigo, final String nome, final DominioFuncaoProfissional funcaoProfiss, final DominioSituacao situacao);

	List<MbcTipoAnestesias> obterMbcTipoAnestesiasPorSituacao(final DominioSituacao situacao);
	
	List<MbcTipoAnestesias> listarTiposAnestesiaFiltro(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Short codigo, final String descricaoTipo, final Boolean necessitaAnestesia, final Boolean tipoCombinado, final DominioSituacao situacaoProfissional);
	
	Long listarTiposAnestesiaFiltroCount(final Short codigo, final String descricaoTipo, final Boolean necessitaAnestesia, final Boolean tipoCombinado, final DominioSituacao situacaoProfissional);
	
	List<MbcTipoAnestesias> obterMbcTipoAnestesiasAtivas();
	
	List<MbcTipoAnestesiaCombinada> listarTiposAnestesiaCombinadas(final MbcTipoAnestesias tipoAnestesia);
		
	Long pesquisarProcedimentosUsadosEquipeCount(MbcProcPorEquipe elemento);
	
	List<MbcSalaCirurgica> buscarSalaCirurgica(MbcSalaCirurgica salaCirurgica);
	
	List<MbcMvtoSalaCirurgica> buscarHistoricoSalaCirurgica(Short seqp, Short unfSeq);
	
	MbcSalaCirurgica obterSalaCirurgicaBySalaCirurgicaId(Short seqp, Short unfSeq);
	
	void persistirMbcSalaCirurgica(MbcSalaCirurgica mbcSalaCirurgica) throws BaseException;
	
	void persistirMbcMvtoSalaCirurgica(MbcMvtoSalaCirurgica mbcSalaCirurgica) throws BaseException;
	
	List<MbcAreaTricotomia> pesquisarAreaTricotomia(Short filtroSeq, String filtroDescricao, DominioSituacao filtroSituacao);
	
	void inserirAreaTricotomia(MbcAreaTricotomia newAreaTricotomia);
	
	void atualizarAreaTricotomia(MbcAreaTricotomia newAreaTricotomia) throws ApplicationBusinessException;
	
	public void persistirProfiUnidade(MbcProfAtuaUnidCirgs profissional) throws ApplicationBusinessException, ApplicationBusinessException, ApplicationBusinessException;
	
	public void persistirTipoAnestesia(MbcTipoAnestesias tipoAnestesia) throws ApplicationBusinessException;
	
	public void persistirNecessidade(MbcNecessidadeCirurgica necessidade) throws BaseException;
	
	public void removerMbcNecessidadeCirurgica(MbcNecessidadeCirurgica necessidadeCirurgica) throws BaseException;
	
	public void persistirTipoAnestesiaComb(MbcTipoAnestesiaCombinada tipoAnestesiaCombinada) throws ApplicationBusinessException;
	
	public List<MbcTipoAnestesias> pequisarTiposAnestesiaSB(final String strPesquisa, Boolean indCombinada);
	
	public Long pequisarTiposAnestesiaSBCount(final String strPesquisa, Boolean indCombinada);
	
	public void excluirProfiUnidade(MbcProfAtuaUnidCirgs profissional) throws ApplicationBusinessException, ApplicationBusinessException, ApplicationBusinessException;
	
	void validarPreenchimentoDescricao(String descricao) throws ApplicationBusinessException;
	
	MbcAreaTricotomia obterNovaAreaTricotomia(String descricao);
	
	void inserirProcedimentoUsadoPorEquipe(MbcProcPorEquipe procPorEquipe) throws BaseException;
	
	void removerProcedimentoUsadoPorEquipe(MbcProcPorEquipe procPorEquipe) throws BaseException;
	
	List<MbcProcedimentoCirurgicos> pesquisarProcedimentoCirurgicosPorCodigoDescricaoSituacaoPacsCcih(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc, Integer filtroSeq, String filtroDescricao, DominioSituacao filtroIndSituacao, Boolean filtroIndGeraImagensPacs, 
			Boolean filtroIndInteresseCcih);
	
	Long obterCountProcedimentoCirurgicosPorCodigoDescricaoSituacaoPacsCcih(Integer filtroSeq, String filtroDescricao, 
			DominioSituacao filtroIndSituacao, Boolean filtroIndGeraImagensPacs, Boolean filtroIndInteresseCcih);
	
	List<MbcpProcedimentoCirurgicoVO> obterCursorMbcpProcedimentoCirurgicoVO( final Integer dcgCrgSeq, 
																	 		  final DominioIndRespProc indRespProc,
																			  final DominioSituacao situacao, 
																			  final DominioSituacao indSituacao,
																			  final DominioTipoAtuacao tipoAtuacao, 
																			  final Short unfSeq);

	MbcUnidadeNotaSala obterUnidadeNotaSalaPorChavePrimaria(Object id, Enum[] inner, Enum[] left);
	
	void atualizarMbcEquipamentoCirurgico(MbcEquipamentoCirurgico elemento) throws BaseException;
	
	void inserirMbcEquipamentoCirurgico(MbcEquipamentoCirurgico elemento) throws BaseException;
	
	List<MbcEquipamentoNotaSala> listarEquipamentoNotaSalaPorUnfSeqp(final short UnfSeq, final short seqp);
	
	MbcEquipamentoCirurgico obterMbcEquipamentoCirurgico(String descricao, Short codigo, DominioSituacao situacao);
	
	List<MbcEquipamentoCirgPorUnid> listarEquipamentosCirgPorUnidade(MbcEquipamentoCirurgico equipamentoCirurgico);
	
	void removerEquipamentoCirurgicoPorUnid(MbcEquipamentoCirgPorUnid elemento) throws BaseException;
	
	void inserirEquipamentoCirurgicoPorUnid(MbcEquipamentoCirgPorUnid elemento) throws BaseException;
	
	void atualizarEquipamentoCirurgicoPorUnid(MbcEquipamentoCirgPorUnid elemento) throws BaseException;
	
	MbcProcedimentoCirurgicos obterProcedimentoCirurgico(Integer seq);

	List<MbcProcedimentoPorGrupo> listarMbcProcedimentoPorGrupoPorMbcGrupoProcedCirurgico(final MbcGrupoProcedCirurgico mbcGrupoProcedCirurgico);
	
	MbcProcedimentoPorGrupo obterMbcProcedimentoPorGrupoPorChavePrimaria(MbcProcedimentoPorGrupoId id);
	
	void persistirMbcProcedimentoPorGrupo(MbcProcedimentoPorGrupo mbcProcedimentoPorGrupo) throws ApplicationBusinessException;
	
	void removerMbcProcedimentoPorGrupo(MbcProcedimentoPorGrupo mbcProcedimentoPorGrupo);
	
	List<MbcSinonimoProcCirg> buscaSinonimosPeloSeqProcedimento(Integer pciSeq);

	MbcSinonimoProcCirg obterSinonimoProcedimentoCirurgico(
			MbcSinonimoProcCirgId id);

	Short buscaMenorSeqpSinonimosPeloSeqProcedimento(Integer pciSeq);

	void persistirSinonimoProcedCirurgico(MbcSinonimoProcCirg sinonimo, Boolean inserir)
			throws BaseException;
	
	void validarTempoMinimoProcedimentoCirurgico(Short tempoMinimo)
			throws ApplicationBusinessException;
	
	void validarProcedimentoHospitarInternoRelacionado(Integer numeroPHI,
			List<FatConvGrupoItemProced> convGrupoItemProcedList) throws ApplicationBusinessException ;
	
	void validarDescricaoProcedimentoCirurgico(String descricao)
			throws ApplicationBusinessException;
	
	void persistirProcedimentoCirurgico(MbcProcedimentoCirurgicos procedimentoCirurgico) throws BaseException;
	
	void inserirMotivoDemoraSalaRec(MbcMotivoDemoraSalaRec motivoDemoraSalaRec) throws BaseException;

	void atualizarMotivoDemoraSalaRec(MbcMotivoDemoraSalaRec motivoDemoraSalaRec)
			throws BaseException;
	
	List<MbcDestinoPaciente> pesquisarDestinoPaciente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MbcDestinoPaciente elemento);
	
	Long pesquisarDestinoPacienteCount(MbcDestinoPaciente elemento);
	
	void persistirDestinoPaciente(MbcDestinoPaciente destinoPaciente) throws BaseException;
	
	List<MbcMotivoAtraso> pesquisarMotivoAtraso(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MbcMotivoAtraso elemento);
	
	Long pesquisarMotivoAtrasoCount(MbcMotivoAtraso elemento);
	
	void persistirMotivoAtraso(MbcMotivoAtraso motivoAtraso) throws BaseException;

	List<MbcEspecialidadeProcCirgs> buscarEspecialidadesPeloSeqProcedimento(
			Integer pciSeq);

	MbcEspecialidadeProcCirgs obterEspecialidadeProcedimentoCirurgico(
			MbcEspecialidadeProcCirgsId id);

	void persistirEspecialidadeProcedCirurgico(
			MbcEspecialidadeProcCirgs especialidade,
			Boolean inserir)
			throws BaseException;
	
	
	List<MbcMvtoCaractSalaCirg> pesquisarHistoricoAlteracoesCaractSalas(
			MbcSalaCirurgica salaCirurgicaSelecionada,
			MbcCaracteristicaSalaCirg caracteristicaSalaCirgSelecionada);

	List<MbcMvtoSalaEspEquipe> pesquisarHistoricoAlteracoesAlocacaoSalas(
			MbcCaractSalaEsp caractSalaEspSelecionada);

	List<MbcGrupoProcedCirurgico> pesquisarMbcGrupoProcedCirurgico(final MbcGrupoProcedCirurgico grupoProcedCirurgico);
	
	MbcGrupoProcedCirurgico obterMbcGrupoProcedCirurgicoPorChavePrimaria(final Short seq);
	
	void persistirMbcGrupoProcedCirurgico(MbcGrupoProcedCirurgico mbcGrupoProcedCirurgico);
	
	void removerMbcGrupoProcedCirurgico(MbcGrupoProcedCirurgico mbcGrupoProcedCirurgico) throws ApplicationBusinessException;

	List<MbcSalaCirurgica> buscarSalaCirurgica(Short seqp,
			Short unfSeq, String nome, DominioTipoSala tipoSala,
			Boolean visivelMonitor, DominioSituacao situacao, Boolean asc, MbcSalaCirurgica.Fields ... orders);
	
	Long buscarSalaCirurgicaCount(Short seqp, Short unfSeq, String nome,
			DominioTipoSala tipoSala, Boolean visivelMonitor,
			DominioSituacao situacao);
	
	List<MbcSalaCirurgica> buscarSalasCirurgicas(final String filtro, final Short unfSeq, final DominioSituacao situacao);
	
	List<MbcSalaCirurgica> pesquisarSalasCirurgicasPorUnfSeqSeqpOuNome(Object objSalaCirurgica, AghUnidadesFuncionais unidadeFuncional);
	
	Long pesquisarSalasCirurgicasPorUnfSeqSeqpOuNomeCount(Object objSalaCirurgica, AghUnidadesFuncionais unidadeFuncional);
	
	List<MbcCompSangProcCirg> buscarAsscComponenteSangPeloSeqProcedimento(
			Integer pciSeq);

	MbcCompSangProcCirg buscarComponenteSanguineoEEspecialidadePorSeq(Short seq);

	void persistirCompSangProcedCirurgico(MbcCompSangProcCirg componente,
			 Boolean inserir) throws BaseException;

	void removerCompSangProcedCirurgico(MbcCompSangProcCirg componente);
	
	public List<MbcCaracteristicaSalaCirg> buscarHorariosCaractPorSalaCirurgica(Short unfSeq, Short seqp);

	public List<MbcCaractSalaEsp> pesquisarCaractSalaEspPorCaracteristica(Short casSeq, DominioSituacao situacao);

	public MbcMotivoCancelamento obterMbcMotivoCancelamento(Short codigo);
	
	public List<MbcPerfilCancelamento> listarPerfisCancelamentos(Short mtcSeq);
	
	public void inserirMbcMotivoCancelamento(MbcMotivoCancelamento elemento) throws BaseException;
	
	public void atualizarMbcMotivoCancelamento(MbcMotivoCancelamento elemento) throws BaseException;

	public void inserirMbcPerfilCancelamento(MbcPerfilCancelamento elemento) throws BaseException;
	
	public void removerMbcPerfilCancelamento(MbcPerfilCancelamento elemento) throws BaseException;
		
	List<MbcAreaTricotomia> pesquisarPorSeqOuDescricao(Object parametro);

	List<MbcAreaTricProcCirg> buscarAreaTricPeloSeqProcedimento(Integer pciSeq);

	void persistirAreaTricProcedCirurgico(MbcAreaTricProcCirg area
			) throws BaseException;

	void removerAreaTricProcedCirurgico(MbcAreaTricProcCirg area);

	MbcAreaTricProcCirg obterAreaTricProcedimentoCirurgico(
			MbcAreaTricProcCirgId id);
	List<VMbcProfServidor> pesquisarProfServidorMPFouMCOPorMatriculaOuNomePessoa(String parametro, Short unfSeq, Integer maxResults);
	
	Long pesquisarProfServidorMPFouMCOPorMatriculaOuNomePessoaCount(String parametro, Short unfSeq);

	void persistirMaterialImpNotaSalaUn(MbcMaterialImpNotaSalaUn materialNotaSala) throws BaseException;
	
	void removerMaterialImpNotaSalaUn(MbcMaterialImpNotaSalaUn materialNotaSala) throws BaseException;
	
	void validarItemMaterialNotaSala(MbcMaterialImpNotaSalaUn materialNotaSala) throws BaseException;
	
	List<CadastroMateriaisImpressaoNotaSalaVO> pesquisarCadastroMateriaisImpressaoNotaSala(MbcUnidadeNotaSala unidadeNotaSala);
	
	List<ScoMaterial> pesquisarMateriaisAtivosGrupoMaterial(Object objPesquisa) throws ApplicationBusinessException;
	
	MbcMaterialImpNotaSalaUn obterMaterialImpNotaSalaUn(Integer chavePrimaria);

	MbcUnidadeNotaSala obterUnidadeNotaSalaPorChavePrimaria(Object chavePrimaria);
	
	public void gravarMbcCaractSalaEsp(MbcCaractSalaEsp caractSalaEsp) throws BaseException;

	public MbcCaracteristicaSalaCirg obterCaracteristicaSalaCirgPorPK(Short seq);

	public List<MbcHorarioTurnoCirg> buscarTurnosPorUnidadeFuncionalSb(Object param, Short unfSeq);
	
	public Long buscarTurnosPorUnidadeFuncionalSbCount(Object param, Short unfSeq);

	public void gravarMbcCaracteristicaSalaCirg(MbcCaracteristicaSalaCirg caracteristicaSalaCirg) throws BaseException;

	public MbcCaractSalaEsp obterCaractSalaEspPorPK(MbcCaractSalaEspId id);

	public MbcProfAtuaUnidCirgs obterMbcProfAtuaUnidCirgsPorId(MbcProfAtuaUnidCirgsId id);

	public void persistirMbcTurnos(MbcTurnos turno) throws ApplicationBusinessException;

	public List<MbcTurnos> pesquisarTiposTurnoPorFiltro(MbcTurnos turnoFiltro);

	public List<MbcControleEscalaCirurgica> pesquisarEscalasCirurgicas(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghUnidadesFuncionais unidadeFunc);

	public Long pesquisarEscalasCirurgicasCount(AghUnidadesFuncionais unidadeFunc);

	public void inserirMbcQuestao(MbcQuestao elemento) throws BaseException;
	
	public void atualizarMbcQuestao(MbcQuestao elemento) throws BaseException;

	public void atualizarMbcValorValidoCanc(MbcValorValidoCanc elemento) throws BaseException;
	
	public void inserirMbcValorValidoCanc(MbcValorValidoCanc elemento) throws BaseException;
	
	public List<MbcTipoSala> pesquisarTipoSalas(Short seq, String descricao, DominioSituacao situacao);

	public void gravarMbcTipoSala(MbcTipoSala tpSala) throws ApplicationBusinessException;
	
	public List<MbcTipoSala> buscarTipoSalaAtivasPorCodigoOuDescricao(Object objPesquisa);

	public Long contarTipoSalaAtivasPorCodigoOuDescricao(Object objPesquisa);

	public MbcTurnos obterMbcTurnodById(String valor);

	public List<MbcTurnos> pesquisarTiposTurno(Object objPesquisa);

	public Long pesquisarTiposTurnoCount(Object objPesquisa);
	
	List<LinhaReportVO> pesquisarEspecialidadePorTipoProcCirgs(String strPesquisa);
	
	Long pesquisarEspecialidadePorTipoProcCirgsCount(String strPesquisa);

	Long pesquisarProcDiagTerapCount(Integer seq, String descricao, Short especialidade, DominioIndContaminacao contaminacao);

	List<LinhaReportVO> pesquisarProcDiagTerap(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer seq,
			String descricao, Short especialidade, DominioIndContaminacao contaminacao);

	String removerPdtEquipPorProc(PdtEquipPorProc equipPorProc
			);

	String removerPdtInstrPorEquip(PdtInstrPorEquip instrPorEquip
			);

	List<PdtInstrPorEquip> listarPdtInstrPorEquipAtivoPorEquip(Short seq);

	String persistirPdtEquipPorProc(PdtEquipamento equipamento,
			PdtProcDiagTerap procDiagTerap)
			throws ApplicationBusinessException;

	String persistirPdtInstrPorEquip(PdtEquipamento equipamento,
			PdtInstrumental instrumental)
			throws ApplicationBusinessException;

	String persistirPdtEquipamento(PdtEquipamento equipamento
			) throws ApplicationBusinessException;

	public String persistirPdtInstrumental(PdtInstrumental instrumental) throws ApplicationBusinessException;
	
	public String persistirPdtInstrPorEquip(PdtInstrPorEquip instrPorEquip) throws ApplicationBusinessException;
	
	String removerPdtInstrumental(PdtInstrumental instrumental);
	
	PdtInstrumental obterPdtInstrumentalPorSeq(Integer seq);

	Long listarPdtInstrumentalPorSeqDescricaoSituacaoCount(Integer seq,
			String descricao, DominioSituacao situacao);
	
	List<PdtInstrumental> listarPdtInstrumentalAtivaPorDescricao(Object strPesquisa);
	
	Long listarPdtInstrumentalAtivaPorDescricaoCount(Object strPesquisa);
	
	List<PdtInstrumental> pesquisarPdtInstrumental(final String strPesquisa, final Short deqSeq);
	
	Long pesquisarPdtInstrumentalCount(final String strPesquisa, final Short deqSeq);
	
	List<PdtInstrumental> listarPdtInstrumentalPorSeqDescricaoSituacao(
			Integer seq, String descricao, DominioSituacao situacao,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc);

	void refreshPdtEquipPorProc(List<PdtEquipPorProc> listPdtEquipPorProc);

	void refreshPdtInstrPorEquip(List<PdtInstrPorEquip> listInstrPorEquip);
	
	PdtInstrPorEquip obterPdtInstrPorEquipPorId(PdtInstrPorEquipId id);
	
	List<PdtInstrPorEquip> listarPdtInstrPorEquip(Integer seqInstrumento);
	
	void atualizarServidor() throws ApplicationBusinessException;
	
	List<MbcProfAtuaUnidCirgs> pesquisarProfissionaisUnidCirgAtivoPorServidorUnfSeq(final RapServidores servidor, final Short unf_Seq);
	
	MbcProfAtuaUnidCirgs pesquisarProfUnidCirgAtivoPorServidorUnfSeqFuncao(final RapServidores servidor, final Short unf_Seq, 
			final DominioFuncaoProfissional funcao);
			
	List<MbcProfAtuaUnidCirgs> pesquisarProfissionaisUnidCirgAtivoPorServidorUnfSeq(final RapServidores servidor, final Short unf_Seq, final boolean considerarFuncoes);
	
	List<MbcNecessidadeCirurgica> buscarNecessidadeCirurgicaPorCodigoOuDescricao(String parametro, boolean somenteAtivos);

	Long countBuscarNecessidadeCirurgicaPorCodigoOuDescricao(String parametro, boolean somenteAtivos);

	String persistirPdtDescPadrao(PdtDescPadrao descricaoPadrao) throws ApplicationBusinessException;
	
	String excluirPdtDescPadrao(PdtDescPadrao descricaoPadrao);

	List<LinhaReportVO> listarEspecialidadePorNomeOuSigla(String parametro);

	Long listarEspecialidadePorNomeOuSiglaCount(String parametro);
	
	Long pesquisarEspecialidadeEquipeSalaCount(MbcSalaCirurgica sala, 
			MbcProfAtuaUnidCirgs profAtuaUnidcirgs, Date date);

	List<MbcCaracteristicaSalaCirg> pesquisarSalaDiaSemanaTurno(Object objSalaDiaSemanaTurno, AghUnidadesFuncionais unidadeFuncional);
	
	public Long pesquisarSalaDiaSemanaTurnoCount(Object objSalaDiaSemanaTurno, AghUnidadesFuncionais unidadeFuncional);
	
	public String excluirEscalaProfissionaisPorSala(MbcEscalaProfUnidCirg escalaExclusao);
	
	void inserirMbcEscalaProfUnidCirg(MbcEscalaProfUnidCirg novoCaractSalaCirg) throws ApplicationBusinessException;

	void deletarMbcEscalaProfUnidCirg(MbcEscalaProfUnidCirg escalaProfUnidCirg);
	
	public List<MbcEscalaProfUnidCirg> pesquisarProfissionaisEscalaPorSala(AghUnidadesFuncionais unidadeFuncional,
																			MbcSalaCirurgica salaCirurgica, 
																			DominioDiaSemana diaSemana,
																			MbcTurnos turnos, 
																			DominioFuncaoProfissional funcaoProfissional,
																			RapServidores profissionalServ, 
																			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);

	public Long pesquisarProfissionaisEscalaPorSalaCount(AghUnidadesFuncionais unidadeFuncional,
															MbcSalaCirurgica salaCirurgica, 
															DominioDiaSemana diaSemana,
															MbcTurnos turnos, 
															DominioFuncaoProfissional funcaoProfissional,
															RapServidores profissionalServ);

	public List<MbcProfAtuaUnidCirgs> pesquisarFuncaoProfissionalEscala(Object objFuncaoProfissional, AghUnidadesFuncionais unidadeFuncional);
	
	public String pesquisarUnidadeTurno(Short unidadeFuncional, String turno);

	List<RelatorioEscalaProfissionaisSemanaVO> buscarDadosRelatorioEscalaProfissionaisSemana(AghUnidadesFuncionais unidadeFuncional,
																							 	MbcTurnos turnos,
																								DominioFuncaoProfissional funcaoProfissional1,
																								DominioFuncaoProfissional funcaoProfissional2,
																								DominioFuncaoProfissional funcaoProfissional3,
																								DominioFuncaoProfissional funcaoProfissional4) throws ApplicationBusinessException;
	
	RelatorioResumoCirurgiasRealizadasPorPeriodoVO buscaDadosRelatorio(AghUnidadesFuncionais unidadeCirurgica, Date dataInicial, Date dataFinal) throws ApplicationBusinessException;

	List<RelatorioResumoCirurgiasRealizadasPorPeriodoListVO> buscaDadosRelatorioDetalhe(AghUnidadesFuncionais unidadeCirurgica, Date dataInicial, Date dataFinal) throws ApplicationBusinessException;

	Boolean verificarCirurgiaPossuiComponenteSanguineo(Integer crgSeq);
	
	List<DominioFuncaoProfissional> listarDominioFuncaoPorMedico();

	MbcGrupoAlcadaAvalOpms persistirGrupoAlcada(MbcGrupoAlcadaAvalOpms grupoAlcada, RapServidores rapServidores);

	MbcGrupoAlcadaAvalOpms buscaGrupoAlcada(
			DominioTipoConvenioOpms tipoConvenio,
			AghEspecialidades aghEspecialidades);

	void alteraGrupoAnterior(MbcGrupoAlcadaAvalOpms grupoAlcadaVersaoAnterior, RapServidores rapServidores) throws ApplicationBusinessException;

	void alterarSituacao(MbcGrupoAlcadaAvalOpms grupoAlcada,RapServidores rapServidores);
	
	void persistirMbcServidorAvalOpms(MbcServidorAvalOpms elemento)throws ApplicationBusinessException, ApplicationBusinessException;
	
	MbcEspecialidadeProcCirgs obterEspecialidadeProcedimentoCirurgico(
			MbcEspecialidadeProcCirgsId id, Enum[] inner, Enum[] left);
	
	List<MbcAlcadaAvalOpms> buscaNiveisAlcadaAprovacaoPorGrupo(Short seq) throws ApplicationBusinessException;
	
	MbcAlcadaAvalOpms buscaNivelAlcada(MbcAlcadaAvalOpms nivelAlcada) throws ApplicationBusinessException;

	void removerServidor(MbcServidorAvalOpms servidor);

	void ativarDesativarServidor(MbcServidorAvalOpms servidor);


	void persistirNivelAlcada(MbcAlcadaAvalOpms nivelAlcadaInsercao) throws ApplicationBusinessException;

	void persistirNiveisAlcada(List<MbcAlcadaAvalOpms> listaAlcadaCriacao) throws ApplicationBusinessException;

	void removerNivelAlcada(Short seqNivelExcluir) throws ApplicationBusinessException;

	MbcGrupoAlcadaAvalOpms buscaGrupoAlcadaPorSequencial(Short codigoGrupo);

	List<MbcServidorAvalOpms> buscaServidoresPorNivelAlcada(MbcAlcadaAvalOpms nivelAlcada);

	MbcServidorAvalOpms buscaServidoresPorSeq(Short seq);

	MbcGrupoAlcadaAvalOpms buscarGrupoAlcadaAtivo(DominioTipoConvenioOpms tipoConvenio,AghEspecialidades aghEspecialidades);

	Long buscarNotasSalaPorUnidadeCirurgicaCount(Short seq);
	
	List<AghUnidadesFuncionais> buscarUnidadesFuncionaisCirurgiaSB(Object param);

	void verificarExisteRegistroPdtDescPadrao(PdtDescPadrao descricaoPadrao) throws ApplicationBusinessException;

	Long pesquisarMateriaisAtivosGrupoMaterialCount(Object objPesquisa) throws ApplicationBusinessException;

	Long buscarSalasCirurgicasCount(String filtro, Short seq, DominioSituacao a);
	
	List<HistoricoAlteracoesGrupoAlcadaVO> buscarHistoricoGrupoAlcada(Short seq);

	List<MbcAlcadaAvalOpms> buscaNiveisAlcadaAprovacaoPorGrupoValor(Short seq)
			throws ApplicationBusinessException;

	void atualizaNivelAlacada(MbcAlcadaAvalOpms nivel);

	List<MbcGrupoAlcadaAvalOpms> validaGrupoEspecialidadeConvenio(
			MbcGrupoAlcadaAvalOpms item);

	void atualizarMbcTurnos(MbcTurnos turno) throws ApplicationBusinessException;

}