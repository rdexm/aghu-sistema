package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.MbcGrupoAlcadaRN;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.vo.CadastroMateriaisImpressaoNotaSalaVO;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.vo.RelatorioEscalaProfissionaisSemanaVO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAlcadaAvalOpmsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAreaTricProcCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAreaTricotomiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCompSangProcCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDestinoPacienteDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoNotaSalaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEscalaProfUnidCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEspecialidadeProcCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcGrupoAlcadaAvalOpmsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcGrupoProcedCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMaterialImpNotaSalaUnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMotivoAtrasoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMotivoCancelamentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMvtoCaractSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMvtoSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMvtoSalaEspEquipeDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcNecessidadeCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcPerfilCancelamentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcPorEquipeDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoPorGrupoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcServidorAvalOpmsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSinonimoProcedCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcTipoAnestesiaCombinadaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcTipoAnestesiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcTipoSalaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcTurnosDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcUnidadeNotaSalaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtInstrPorEquipDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtInstrumentalDAO;
import br.gov.mec.aghu.blococirurgico.dao.VMbcProfServidorDAO;
import br.gov.mec.aghu.blococirurgico.vo.HistoricoAlteracoesGrupoAlcadaVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcpProcedimentoCirurgicoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioResumoCirurgiasRealizadasPorPeriodoListVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioResumoCirurgiasRealizadasPorPeriodoVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
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


@Modulo(ModuloEnum.BLOCO_CIRURGICO)
@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects"})
@Stateless
public class BlocoCirurgicoCadastroApoioFacade extends BaseFacade implements IBlocoCirurgicoCadastroApoioFacade {

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;
	
	@Inject
	private MbcAlcadaAvalOpmsDAO mbcAlcadaAvalOpmsDAO;
	
	@Inject
	private MbcGrupoAlcadaAvalOpmsDAO mbcGrupoAlcadaAvalOpmsDAO;

	@Inject
	private MbcTipoSalaDAO mbcTipoSalaDAO;

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@Inject
	private MbcEscalaProfUnidCirgDAO mbcEscalaProfUnidCirgDAO;

	@Inject
	private MbcNecessidadeCirurgicaDAO mbcNecessidadeCirurgicaDAO;

	@Inject
	private PdtInstrPorEquipDAO pdtInstrPorEquipDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;

	@Inject
	private MbcGrupoProcedCirurgicoDAO mbcGrupoProcedCirurgicoDAO;

	@Inject
	private MbcProcPorEquipeDAO mbcProcPorEquipeDAO;

	@Inject
	private MbcAreaTricotomiaDAO mbcAreaTricotomiaDAO;

	@Inject
	private MbcServidorAvalOpmsDAO mbcServidorAvalOpmsDAO;

	@Inject
	private MbcTipoAnestesiasDAO mbcTipoAnestesiasDAO;

	@Inject
	private MbcMotivoAtrasoDAO mbcMotivoAtrasoDAO;

	@Inject
	private MbcCompSangProcCirgDAO mbcCompSangProcCirgDAO;

	@Inject
	private MbcTipoAnestesiaCombinadaDAO mbcTipoAnestesiaCombinadaDAO;

	@Inject
	private MbcPerfilCancelamentoDAO mbcPerfilCancelamentoDAO;

	@Inject
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirgDAO;

	@Inject
	private MbcSinonimoProcedCirurgicoDAO mbcSinonimoProcedCirurgicoDAO;

	@Inject
	private MbcMvtoSalaCirurgicaDAO mbcMvtoSalaCirurgicaDAO;

	@Inject
	private MbcProcedimentoPorGrupoDAO mbcProcedimentoPorGrupoDAO;

	@Inject
	private MbcMaterialImpNotaSalaUnDAO mbcMaterialImpNotaSalaUnDAO;

	@Inject
	private MbcCaractSalaEspDAO mbcCaractSalaEspDAO;

	@Inject
	private MbcSalaCirurgicaDAO mbcSalaCirurgicaDAO;

	@Inject
	private MbcTurnosDAO mbcTurnosDAO;

	@Inject
	private MbcAreaTricProcCirgDAO mbcAreaTricProcCirgDAO;

	@Inject
	private MbcEquipamentoNotaSalaDAO mbcEquipamentoNotaSalaDAO;

	@Inject
	private MbcMvtoCaractSalaCirgDAO mbcMvtoCaractSalaCirgDAO;

	@Inject
	private MbcMvtoSalaEspEquipeDAO mbcMvtoSalaEspEquipeDAO;

	@Inject
	private MbcMotivoCancelamentoDAO mbcMotivoCancelamentoDAO;

	@Inject
	private MbcUnidadeNotaSalaDAO mbcUnidadeNotaSalaDAO;

	@Inject
	private MbcDestinoPacienteDAO mbcDestinoPacienteDAO;

	@Inject
	private VMbcProfServidorDAO vMbcProfServidorDAO;

	@Inject
	private PdtInstrumentalDAO pdtInstrumentalDAO;

	@Inject
	private MbcEspecialidadeProcCirgsDAO mbcEspecialidadeProcCirgsDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;


	@EJB
	private EquipamentosDiagnosticoTerapeuticoON equipamentosDiagnosticoTerapeuticoON;

	@EJB
	private MateriaisImpressaoNotaSalaON materiaisImpressaoNotaSalaON;

	@EJB
	private PesquisaNiveisAlcadaAprovacaoRN pesquisaNiveisAlcadaAprovacaoRN;

	@EJB
	private MbcCidUsualEquipeRN mbcCidUsualEquipeRN;

	@EJB
	private MbcGrupoAlcadaRN2 mbcGrupoAlcadaRN2;

	@EJB
	private MbcGrupoAlcadaRN mbcGrupoAlcadaRN;

	@EJB
	private MbcMotivoAtrasoRN mbcMotivoAtrasoRN;

	@EJB
	private MbcNecessidadeCirurgicaRN mbcNecessidadeCirurgicaRN;

	@EJB
	private MbcDestinoPacienteRN mbcDestinoPacienteRN;

	@EJB
	private SalaCirurgicaON salaCirurgicaON;

	@EJB
	private MbcProcedimentoPorGrupoRN mbcProcedimentoPorGrupoRN;

	@EJB
	private AreaTricotomiaProcCirgRN areaTricotomiaProcCirgRN;

	@EJB
	private ProfissionalUnidCirurgicaRN profissionalUnidCirurgicaRN;

	@EJB
	private MbcServidorAvalOpmsRN mbcServidorAvalOpmsRN;

	@EJB
	private ProcedimentoCirurgicoON procedimentoCirurgicoON;

	@EJB
	private MbcEscalaProfUnidCirgRN mbcEscalaProfUnidCirgRN;

	@EJB
	private ComponenteSangProcCirgRN componenteSangProcCirgRN;

	@EJB
	private EditaNiveisAlcadaAprovacaoRN editaNiveisAlcadaAprovacaoRN;

	@EJB
	private EspecialidadeProcCirgRN especialidadeProcCirgRN;

	@EJB
	private PdtInstrumentalON pdtInstrumentalON;

	@EJB
	private MbcHorarioTurnoCirgON mbcHorarioTurnoCirgON;

	@EJB
	private ManterProcedimentosUsadosEquipeRN manterProcedimentosUsadosEquipeRN;

	@EJB
	private MbcMotivoDemoraSalaRecRN mbcMotivoDemoraSalaRecRN;

	@EJB
	private MbcMotivoCancelamentoRN mbcMotivoCancelamentoRN;

	@EJB
	private AreaTricotomiaON areaTricotomiaON;

	@EJB
	private RelatorioResumoCirurgiasRealizadasPorPeriodoON relatorioResumoCirurgiasRealizadasPorPeriodoON;

	@EJB
	private MbcTipoSalaON mbcTipoSalaON;

	@EJB
	private EquipamentosNotaSalaRN equipamentosNotaSalaRN;

	@EJB
	private ProcedimentoDiagnosticoTerapeuticoON procedimentoDiagnosticoTerapeuticoON;

	@EJB
	private RelatorioEscalaProfissionaisSemanaON relatorioEscalaProfissionaisSemanaON;

	@EJB
	private MbcPerfilCancelamentoRN mbcPerfilCancelamentoRN;

	@EJB
	private MapeamentoSalasON mapeamentoSalasON;

	@EJB
	private SinonimoRN sinonimoRN;

	@EJB
	private MbcGrupoProcedCirurgicoRN mbcGrupoProcedCirurgicoRN;

	@EJB
	private MbcUnidadeNotaSalaRN mbcUnidadeNotaSalaRN;

	@EJB
	private TipoAnestesiasRN tipoAnestesiasRN;

	@EJB
	private MbcQuestaoRN mbcQuestaoRN;

	@EJB
	private MbcTurnosRN mbcTurnosRN;

	@EJB
	private PdtInstrumentalRN pdtInstrumentalRN;

	@EJB
	private AreaTricotomiaRN areaTricotomiaRN;

	@EJB
	private MbcValorValidoCancRN mbcValorValidoCancRN;

	@EJB
	private MbcEquipamentoCirurgicoON mbcEquipamentoCirurgicoON;

	@EJB
	private PdtDescricaoPadraoON pdtDescricaoPadraoON;

	@EJB
	private MbcMvtoSalaCirurgicaRN mbcMvtoSalaCirurgicaRN;

	@EJB
	private TipoAnestesiasCombinadasRN tipoAnestesiasCombinadasRN;

	@EJB
	private MbcMaterialImpNotaSalaUnRN mbcMaterialImpNotaSalaUnRN;
	private static final long serialVersionUID = 2317099895565412647L;

	@Override
	public List<AghUnidadesFuncionais> buscarUnidadesFuncionaisCirurgia(final Object objPesquisa) {
		return getMbcHorarioTurnoCirgDAO().buscarUnidadesFuncionaisCirurgia(objPesquisa);
	}
	@Override
	public Long contarUnidadesFuncionaisCirurgia(final Object objPesquisa) {
		return getMbcHorarioTurnoCirgDAO().contarUnidadesFuncionaisCirurgia(objPesquisa);
	}
	@Override
	@Secure("#{s:hasPermission('cadastroTurno', 'pesquisar')}")
	public List<MbcHorarioTurnoCirg> buscarMbcHorarioTurnoCirg(final MbcHorarioTurnoCirg mbcHorarioTurnoCirg) {
		return getMbcHorarioTurnoCirgDAO().buscarMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
	}
	@Override
	@Secure("#{s:hasPermission('cadastroTurno', 'excluir')}")
	public void excluirMbcHorarioTurnoCirg(final MbcHorarioTurnoCirgId id) throws ApplicationBusinessException {
		this.getMbcHorarioTurnoCirgON().excluirMbcHorarioTurnoCirg(id);
	}
	@Override
	@Secure("#{s:hasPermission('cadastroTurno', 'persistir')}")
	public void persistirMbcHorarioTurnoCirg(final MbcHorarioTurnoCirg mbcHorarioTurnoCirg, final String nomeMicrocomputador) throws BaseException {
		this.getMbcHorarioTurnoCirgON().persistirMbcHorarioTurnoCirg(mbcHorarioTurnoCirg, nomeMicrocomputador); 
	}
	@Override
	public MbcHorarioTurnoCirg obterMbcHorarioTurnoCirgPorId(final MbcHorarioTurnoCirgId id){
		return this.getMbcHorarioTurnoCirgDAO().obterPorChavePrimaria(id);
	}
	protected MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO(){
		return mbcHorarioTurnoCirgDAO;
	}
	protected MbcHorarioTurnoCirgON getMbcHorarioTurnoCirgON() {
		return mbcHorarioTurnoCirgON;
	}
	@Override
	@Secure("#{s:hasPermission('cidPorEquipeCadastro','gravar')}")
	public void atualizarCidUsualEquipe(MbcCidUsualEquipe elemento) throws BaseException {
		this.getMbcCidUsualEquipeRN().atualizar(elemento);
	}
	@Override
	@Secure("#{s:hasPermission('cidPorEquipeCadastro','gravar')}")
	public void inserirCidUsualEquipe(MbcCidUsualEquipe elemento) throws BaseException {
		this.getMbcCidUsualEquipeRN().inserir(elemento);
	}
	protected MbcCidUsualEquipeRN getMbcCidUsualEquipeRN() {
		return mbcCidUsualEquipeRN;
	}
	
    @Override
    public Long pesquisarProcedimentosCirurgicosPorCodigoDescricaoCount(Object filtro, DominioSituacao situacao) {
            return getMbcProcedimentoCirurgicoDAO().pesquisarProcedimentosCirurgicosPorCodigoDescricaoCount(filtro, situacao);
    }
	
    @Override
    public List<MbcProcedimentoCirurgicos> pesquisarProcedimentosCirurgicosPorCodigoDescricao(Object filtro, String order, Integer maxResults, DominioSituacao situacao) {
            return getMbcProcedimentoCirurgicoDAO().pesquisarProcedimentosCirurgicosPorCodigoDescricao(filtro, order, maxResults, situacao);
    }
    
	@Override
	public List<MbcProcedimentoCirurgicos> pesquisaProcedimentoCirurgicosPorCodigoDescricaoSituacao(Object filtro, DominioSituacao situacao, String order){
		return getMbcProcedimentoCirurgicoDAO().pesquisaProcedimentoCirurgicosPorCodigoDescricaoSituacao(filtro, situacao, order);
	}
	
	@Override
	public MbcProcedimentoCirurgicos obterProcedimentoCirurgico(Integer seq) {
		return getMbcProcedimentoCirurgicoDAO().obterPorChavePrimaria(seq);
	}
	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO() {
		return mbcProcedimentoCirurgicoDAO;
	}
	@Override
	public List<MbcProcedimentoPorGrupo> listarMbcProcedimentoPorGrupoPorMbcGrupoProcedCirurgico(final MbcGrupoProcedCirurgico mbcGrupoProcedCirurgico){
		return getMbcProcedimentoPorGrupoDAO().listarMbcProcedimentoPorGrupoPorMbcGrupoProcedCirurgico(mbcGrupoProcedCirurgico);
	} 
	@Override
	public MbcProcedimentoPorGrupo obterMbcProcedimentoPorGrupoPorChavePrimaria(MbcProcedimentoPorGrupoId id){
		return getMbcProcedimentoPorGrupoDAO().obterPorChavePrimaria(id);
	}
	@Override
	public void persistirMbcProcedimentoPorGrupo(MbcProcedimentoPorGrupo mbcProcedimentoPorGrupo) throws ApplicationBusinessException {
		getMbcProcedimentoPorGrupoRN().persistirMbcProcedimentoPorGrupo(mbcProcedimentoPorGrupo);
	}
	
	public void persistirMbcServidorAvalOpms(MbcServidorAvalOpms elemento) throws ApplicationBusinessException {
		getMbcServidorAvalOpmsRN().persistir(elemento);
	}
	
	protected MbcServidorAvalOpmsRN getMbcServidorAvalOpmsRN() {
		return mbcServidorAvalOpmsRN;
	}
	protected MbcServidorAvalOpmsDAO getMbcServidorAvalOpmsDAO() {
		return mbcServidorAvalOpmsDAO;
	}
	
	@Override
	public void removerMbcProcedimentoPorGrupo(MbcProcedimentoPorGrupo mbcProcedimentoPorGrupo) {
		getMbcProcedimentoPorGrupoRN().removerMbcProcedimentoPorGrupo(mbcProcedimentoPorGrupo);
	}
	protected MbcProcedimentoPorGrupoRN getMbcProcedimentoPorGrupoRN() {
		return mbcProcedimentoPorGrupoRN;
	}
	protected MbcProcedimentoPorGrupoDAO getMbcProcedimentoPorGrupoDAO() {
		return mbcProcedimentoPorGrupoDAO;
	}
	protected MbcEscalaProfUnidCirgDAO getMbcEscalaProfUnidCirgDAO() {
		return mbcEscalaProfUnidCirgDAO;
	}
	@Override
	public List<MbcProcPorEquipe> pesquisarProcedimentosUsadosEquipe(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MbcProcPorEquipe elemento) {
		return getMbcProcPorEquipeDAO().pesquisarProcedimentosUsadosEquipe(firstResult, maxResult, orderProperty, asc, elemento);
	}
	@Override
	public List<MbcControleEscalaCirurgica> pesquisarEscalasCirurgicas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AghUnidadesFuncionais unidadeFunc) {
		return getMbcControleEscalaCirurgicaDAO().pesquisarEscalasCirurgicas(firstResult, maxResult, orderProperty, asc, unidadeFunc);
	}
	@Override
	public Long pesquisarEscalasCirurgicasCount(AghUnidadesFuncionais unidadeFunc) {
		return getMbcControleEscalaCirurgicaDAO().pesquisarEscalasCirurgicasCount(unidadeFunc);
	}
	private MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() {
		return mbcControleEscalaCirurgicaDAO;
	}
	
	protected MbcNecessidadeCirurgicaDAO getMbcNecessidadeCirurgicaDAO() {
		return mbcNecessidadeCirurgicaDAO;
	}
	@Override
	public List<MbcNecessidadeCirurgica> listarNecessidadesFiltro(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc, 
			final Short necesSeq, final String descricaoNecess, final AghUnidadesFuncionais unidFunc, final DominioSituacao situacao, final Boolean requerDescricao){
		return getMbcNecessidadeCirurgicaDAO().listarNecessidadesFiltro(firstResult, maxResult, orderProperty, asc, necesSeq, descricaoNecess, unidFunc, situacao, requerDescricao);
	}
	
	@Override
	public Long listarNecessidadesFiltroCount(final Short necesSeq, final String descricaoNecess, final AghUnidadesFuncionais unidFunc, final DominioSituacao situacao, final Boolean requerDescricao){
		return getMbcNecessidadeCirurgicaDAO().listarNecessidadesFiltroCount(necesSeq, descricaoNecess, unidFunc, situacao, requerDescricao);
	}

	@Override
	public List<MbcProfAtuaUnidCirgs> listarProfissionaisUnidCirFiltro(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final AghUnidadesFuncionais unidadeFuncional, final Integer matricula, final Short vinCodigo, final String nome, final DominioFuncaoProfissional funcaoProfiss, final DominioSituacao situacao) {
		return getMbcProfAtuaUnidCirgsDAO().listarProfissionaisUnidCirFiltro(firstResult, maxResult, orderProperty, asc, unidadeFuncional, matricula, vinCodigo, nome, funcaoProfiss, situacao);
	}
	
	@Override
	public Long listarProfissionaisUnidCirFiltroCount(final AghUnidadesFuncionais unidadeFuncional, final Integer matricula, final Short vinCodigo, final String nome, final DominioFuncaoProfissional funcaoProfiss, final DominioSituacao situacao) {
		return getMbcProfAtuaUnidCirgsDAO().listarProfissionaisUnidCirFiltroCount(unidadeFuncional, matricula,vinCodigo, nome, funcaoProfiss, situacao);
	}

	@Override
	public List<MbcTipoAnestesias> obterMbcTipoAnestesiasPorSituacao(final DominioSituacao situacao){
		return getMbcTipoAnestesiasDAO().obterMbcTipoAnestesiasPorSituacao(situacao);
	}
	
	@Override
	public List<MbcTipoAnestesias> listarTiposAnestesiaFiltro(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Short codigo, final String descricaoTipo, final Boolean necessitaAnestesia, final Boolean tipoCombinado, final DominioSituacao situacaoProfissional){
		return getMbcTipoAnestesiasDAO().listarTiposAnestesiaFiltro(firstResult, maxResult, orderProperty, asc, codigo, descricaoTipo, necessitaAnestesia, tipoCombinado, situacaoProfissional);
	}
	
	@Override
	public Long listarTiposAnestesiaFiltroCount(final Short codigo, final String descricaoTipo, final Boolean necessitaAnestesia, final Boolean tipoCombinado, final DominioSituacao situacaoProfissional){
		return getMbcTipoAnestesiasDAO().listarTiposAnestesiaFiltroCount(codigo, descricaoTipo, necessitaAnestesia, tipoCombinado, situacaoProfissional);
	}

	@Override
	public List<MbcTipoAnestesias> obterMbcTipoAnestesiasAtivas(){
		return getMbcTipoAnestesiasDAO().obterMbcTipoAnestesiasAtivas();
	}
	
	@Override
	public List<MbcTipoAnestesiaCombinada> listarTiposAnestesiaCombinadas(final MbcTipoAnestesias tipoAnestesia){
		return getMbcTipoAnestesiaCombinadaDAO().listarTiposAnestesiaCombinadas(tipoAnestesia);
	}
	

	@Override
	@Secure("#{s:hasPermission('procedimentosMaisUsadosPorEquipe','pesquisar')}")
	public Long pesquisarProcedimentosUsadosEquipeCount(MbcProcPorEquipe elemento) {
		return getMbcProcPorEquipeDAO().pesquisarProcedimentosUsadosEquipeCount(elemento);
	}

	private MbcProcPorEquipeDAO getMbcProcPorEquipeDAO() {
		return mbcProcPorEquipeDAO;
	}

	protected MbcUnidadeNotaSalaDAO getMbcUnidadeNotaSalaDAO() {
		return mbcUnidadeNotaSalaDAO;
	}

	@Override
	public List<MbcUnidadeNotaSala> buscarNotasSalaPorUnidadeCirurgica(final Short unfSeq) {
		return getMbcUnidadeNotaSalaDAO().obterNotaSalaPorUnidade(unfSeq);
	}

	@Override
	@Secure("#{s:hasPermission('impressaoNotaSalaCadastro','executar')}")
	public void excluirNotaDeSala(MbcUnidadeNotaSalaId mbcUnidadeNotaSalaId) throws ApplicationBusinessException{
		getMbcUnidadeNotaSalaRN().excluir(mbcUnidadeNotaSalaId);
	}

	@Override
	@Secure("#{s:hasPermission('impressaoNotaSalaCadastro','executar')}")
	public void persistirNotaDeSala(MbcUnidadeNotaSala mbcUnidadeNotaSala) throws ApplicationBusinessException{
		getMbcUnidadeNotaSalaRN().persistirNotaDeSala(mbcUnidadeNotaSala);
	}
	
	@Override
	@Secure("#{s:hasPermission('equipamentoImpressaoNotaSala','executar')}")
	public void persistirEquipamentoNotaDeSala(MbcEquipamentoNotaSala mbcEquipamentoNotaSala) throws ApplicationBusinessException{
		getEquipamentosNotaSalaRN().persistirEquipamentoNotaDeSala(mbcEquipamentoNotaSala);
	}
	
	@Override
	@Secure("#{s:hasPermission('equipamentoImpressaoNotaSala','executar')}")
	public void excluirEquipamentoNotaDeSala(MbcEquipamentoNotaSalaId equipamentoNotaSalaId){
		getEquipamentosNotaSalaRN().excluirEquipamentoNotaDeSala(equipamentoNotaSalaId);
	}
	
	protected EquipamentosNotaSalaRN getEquipamentosNotaSalaRN(){
		return equipamentosNotaSalaRN;
	}
		
	protected MbcUnidadeNotaSalaRN getMbcUnidadeNotaSalaRN(){
		return mbcUnidadeNotaSalaRN;
	}	
	
	@Override
	@Secure("#{s:hasPermission('cadastroSala', 'pesquisar')}")
	public List<MbcSalaCirurgica> buscarSalaCirurgica(MbcSalaCirurgica salaCirurgica) {
		return getMbcSalaCirurgicaDAO().buscarSalaCirurgica(salaCirurgica);
	}
	
	@Override
	@Secure("#{s:hasPermission('historicoCadastroSalas', 'pesquisar')}")
	public List<MbcMvtoSalaCirurgica> buscarHistoricoSalaCirurgica(Short seqp, Short unfSeq){
		return getMbcMvtoSalaCirurgicaDAO().obterHistoricoSalaCirurgica(seqp, unfSeq);
	}
	
	
	protected MbcSalaCirurgicaDAO getMbcSalaCirurgicaDAO(){
		return mbcSalaCirurgicaDAO;
	}
	
	@Override
	public MbcSalaCirurgica obterSalaCirurgicaBySalaCirurgicaId(Short seqp, Short unfSeq) {
		return getMbcSalaCirurgicaDAO().buscarSalaCirurgicaById(seqp, unfSeq);
	}
	
	@Override
	@Secure("#{s:hasPermission('cadastroSala', 'persistir')}")
	public void persistirMbcSalaCirurgica(MbcSalaCirurgica mbcSalaCirurgica) throws BaseException {
		getSalaCirurgicaON().persistirSalaCirurgica(mbcSalaCirurgica);
	}
	private SalaCirurgicaON getSalaCirurgicaON() {
		return salaCirurgicaON;
	}
	@Override
	public void persistirMbcMvtoSalaCirurgica(MbcMvtoSalaCirurgica mbcSalaCirurgica) throws BaseException {
		getMbcMvtoSalaCirurgicaRN().persistirMbcMvtoSalaCirurgica(mbcSalaCirurgica);
	}

	private MbcMvtoSalaCirurgicaDAO getMbcMvtoSalaCirurgicaDAO() {
		return mbcMvtoSalaCirurgicaDAO;		
	}
	
	private MbcMvtoSalaCirurgicaRN getMbcMvtoSalaCirurgicaRN() {
		return mbcMvtoSalaCirurgicaRN;		
	}
	
	@Override
	public List<MbcAreaTricotomia> pesquisarAreaTricotomia(Short filtroSeq, String filtroDescricao,  DominioSituacao filtroSituacao) {
		return getMbcAreaTricotomiaDAO().pesquisarAreaTricotomia(filtroSeq, filtroDescricao, filtroSituacao);
	}
	
	@Override
	public void inserirAreaTricotomia(MbcAreaTricotomia newAreaTricotomia) {
		getAreaTricotomiaRN().inserirAreaTricotomia(newAreaTricotomia);
	}
	
	@Override
	public void atualizarAreaTricotomia(MbcAreaTricotomia newAreaTricotomia) throws ApplicationBusinessException {
		getAreaTricotomiaRN().atualizarAreaTricotomia(newAreaTricotomia);
	}
	
	@Override
	@Secure("#{s:hasPermission('profissionalUnidadeCirurgico','executar')}")
	public void persistirProfiUnidade(MbcProfAtuaUnidCirgs profissional) throws ApplicationBusinessException, ApplicationBusinessException, ApplicationBusinessException{
		getProfissionalUnidCirurgicaRN().persistirProfiUnidade(profissional);
	}	
	
	@Override
	@Secure("#{s:hasPermission('tiposAnestesias','executar')}")
	public void persistirTipoAnestesia(MbcTipoAnestesias tipoAnestesia) throws ApplicationBusinessException {
		getTipoAnestesiasRN().persistirTipoAnestesia(tipoAnestesia);
	}
	
	@Override
	@Secure("#{s:hasPermission('necessidadeCirurgica','executar')}")
	public void persistirNecessidade(MbcNecessidadeCirurgica necessidade) throws BaseException{
		getMbcNecessidadeCirurgicaRN().persistirMbcNecessidadeCirurgica(necessidade);
	}
	
	@Override
	@Secure("#{s:hasPermission('necessidadeCirurgica','executar')}")
	public void removerMbcNecessidadeCirurgica(MbcNecessidadeCirurgica necessidadeCirurgica) throws BaseException {
		getMbcNecessidadeCirurgicaRN().removerMbcNecessidadeCirurgica(necessidadeCirurgica);
	}
	
	@Override
	public void persistirTipoAnestesiaComb(MbcTipoAnestesiaCombinada tipoAnestesiaCombinada) throws ApplicationBusinessException{
		getTipoAnestesiasCombinadasRN().persistirTipoAnestesiaCombinada(tipoAnestesiaCombinada);
	}
	
	@Override
	@Secure("#{s:hasPermission('profissionalUnidadeCirurgico','executar')}")
	public void excluirProfiUnidade(MbcProfAtuaUnidCirgs profissional) throws ApplicationBusinessException, ApplicationBusinessException, ApplicationBusinessException{
		getProfissionalUnidCirurgicaRN().excluirProfiUnidade(profissional);
	}
	
	@Override
	public List<MbcTipoAnestesias> pequisarTiposAnestesiaSB(final String strPesquisa, Boolean indCombinada){
		return getMbcTipoAnestesiasDAO().pequisarTiposAnestesiaSB(strPesquisa, indCombinada);
	}
	
	@Override
	public Long pequisarTiposAnestesiaSBCount(final String strPesquisa, Boolean indCombinada){
		return getMbcTipoAnestesiasDAO().pequisarTiposAnestesiaSBCount(strPesquisa, indCombinada);
	}
	
	@Override
	public void validarPreenchimentoDescricao(String descricao) throws ApplicationBusinessException {
		getAreaTricotomiaON().validarPreenchimentoDescricao(descricao);
	}
	
	@Override
	public MbcAreaTricotomia obterNovaAreaTricotomia(String descricao) {
		return getAreaTricotomiaON().obterNovaAreaTricotomia(descricao);
	}
	
	@Override
	public List<MbcGrupoProcedCirurgico> pesquisarMbcGrupoProcedCirurgico(final MbcGrupoProcedCirurgico grupoProcedCirurgico) {
		return getMbcGrupoProcedCirurgicoDAO().pesquisarMbcGrupoProcedCirurgico(grupoProcedCirurgico);
	}
	
	@Override
	public MbcGrupoProcedCirurgico obterMbcGrupoProcedCirurgicoPorChavePrimaria(final Short seq) {
		return getMbcGrupoProcedCirurgicoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void removerMbcGrupoProcedCirurgico(MbcGrupoProcedCirurgico mbcGrupoProcedCirurgico) throws ApplicationBusinessException {
		getMbcGrupoProcedCirurgicoRN().removerMbcGrupoProcedCirurgico(mbcGrupoProcedCirurgico);
	}
	
	@Override
	public void persistirMbcGrupoProcedCirurgico(MbcGrupoProcedCirurgico mbcGrupoProcedCirurgico) {
		getMbcGrupoProcedCirurgicoRN().persistirMbcGrupoProcedCirurgico(mbcGrupoProcedCirurgico);
	}

	private MbcGrupoProcedCirurgicoRN getMbcGrupoProcedCirurgicoRN(){
		return mbcGrupoProcedCirurgicoRN;
	}
	
	private MbcGrupoProcedCirurgicoDAO getMbcGrupoProcedCirurgicoDAO() {
		return mbcGrupoProcedCirurgicoDAO;
	}
	
	private MbcAreaTricotomiaDAO getMbcAreaTricotomiaDAO() {
		return mbcAreaTricotomiaDAO;
	}
	
	private AreaTricotomiaRN getAreaTricotomiaRN() {
		return areaTricotomiaRN;
	}
	
	private AreaTricotomiaON getAreaTricotomiaON() {
		return areaTricotomiaON;
	}
	
	private ProfissionalUnidCirurgicaRN getProfissionalUnidCirurgicaRN() {
		return profissionalUnidCirurgicaRN;
	}
	
	private TipoAnestesiasRN getTipoAnestesiasRN() {
		return tipoAnestesiasRN;
	}

	private MbcNecessidadeCirurgicaRN getMbcNecessidadeCirurgicaRN(){
		return mbcNecessidadeCirurgicaRN;
	}
	
	private TipoAnestesiasCombinadasRN getTipoAnestesiasCombinadasRN() {
		return tipoAnestesiasCombinadasRN;
	}
	
	private MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO(){
		return mbcProfAtuaUnidCirgsDAO;
	}
	
	private MbcTipoAnestesiasDAO getMbcTipoAnestesiasDAO(){
		return mbcTipoAnestesiasDAO;
	}

	private MbcTipoAnestesiaCombinadaDAO getMbcTipoAnestesiaCombinadaDAO(){
		return mbcTipoAnestesiaCombinadaDAO;
	}
	
	@Override
	public List<MbcProcedimentoCirurgicos> pesquisarProcedimentoCirurgicosPorCodigoDescricaoSituacaoPacsCcih(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc, Integer filtroSeq, String filtroDescricao, DominioSituacao filtroIndSituacao, Boolean filtroIndGeraImagensPacs, 
			Boolean filtroIndInteresseCcih) {
		return getMbcProcedimentoCirurgicoDAO().pesquisarProcedimentoCirurgicosPorCodigoDescricaoSituacaoPacsCcih(firstResult, maxResult, orderProperty, 
				asc, filtroSeq, filtroDescricao, filtroIndSituacao, filtroIndGeraImagensPacs, filtroIndInteresseCcih);
	}
	
	@Override
	public Long obterCountProcedimentoCirurgicosPorCodigoDescricaoSituacaoPacsCcih(Integer filtroSeq, String filtroDescricao, 
			DominioSituacao filtroIndSituacao, Boolean filtroIndGeraImagensPacs, Boolean filtroIndInteresseCcih) {
		return getMbcProcedimentoCirurgicoDAO().obterCountProcedimentoCirurgicosPorCodigoDescricaoSituacaoPacsCcih(filtroSeq, 
				filtroDescricao, filtroIndSituacao, filtroIndGeraImagensPacs, filtroIndInteresseCcih);
	}

	@Override
	public List<MbcpProcedimentoCirurgicoVO> obterCursorMbcpProcedimentoCirurgicoVO( final Integer dcgCrgSeq, 
																			 		 final DominioIndRespProc indRespProc,
																					 final DominioSituacao situacao, 
																					 final DominioSituacao indSituacao,
																					 final DominioTipoAtuacao tipoAtuacao, 
																					 final Short unfSeq){
		
		return getMbcProcedimentoCirurgicoDAO().obterCursorMbcpProcedimentoCirurgicoVO( dcgCrgSeq, indRespProc, situacao, 
																						indSituacao, tipoAtuacao, unfSeq);
	}
	
	@Override
	@Secure("#{s:hasPermission('procedimentosMaisUsadosPorEquipe','executar')}")
	public void inserirProcedimentoUsadoPorEquipe(MbcProcPorEquipe procPorEquipe) throws BaseException {
		getManterProcedimentosUsadosEquipeRN().inserirMbcProcPorEquipe(procPorEquipe);
	}

	@Override
	@Secure("#{s:hasPermission('procedimentosMaisUsadosPorEquipe','executar')}")
	public void removerProcedimentoUsadoPorEquipe(MbcProcPorEquipe procPorEquipe) throws BaseException {
		getManterProcedimentosUsadosEquipeRN().removerMbcProcPorEquipe(procPorEquipe);
	}
	
	@Override
	public List<MbcSinonimoProcCirg> buscaSinonimosPeloSeqProcedimento(Integer pciSeq) {
		return getMbcSinonimoProcedCirurgicoDAO().buscaSinonimosPeloSeqProcedimento(pciSeq);
	}

	@Override
	public MbcSinonimoProcCirg obterSinonimoProcedimentoCirurgico(MbcSinonimoProcCirgId id) {
		return getMbcSinonimoProcedCirurgicoDAO().obterPorChavePrimaria(id);
	}

	@Override
	public Short buscaMenorSeqpSinonimosPeloSeqProcedimento(Integer pciSeq) {
		return getMbcSinonimoProcedCirurgicoDAO().buscaMenorSeqpSinonimosPeloSeqProcedimento(pciSeq);
	}
	
	@Override
	public void persistirSinonimoProcedCirurgico(MbcSinonimoProcCirg sinonimo, Boolean inserir) throws BaseException {
		getSinonimoRN().persistir(sinonimo, inserir);
	}
	
	@Override
	public void persistirCompSangProcedCirurgico(MbcCompSangProcCirg componente, Boolean inserir) throws BaseException {
		getComponenteSangProcCirgRN().persistir(componente, inserir);
	}

	@Override
	public void removerCompSangProcedCirurgico(MbcCompSangProcCirg componente) {
		getComponenteSangProcCirgRN().remover(componente);
	}
	
	@Override
	public void persistirAreaTricProcedCirurgico(MbcAreaTricProcCirg area) throws BaseException {
		getAreaTricotomiaProcCirgRN().persistir(area);
	}

	@Override
	public void removerAreaTricProcedCirurgico(MbcAreaTricProcCirg area) {
		getAreaTricotomiaProcCirgRN().remover(area);
	}

	@Override
	public MbcAreaTricProcCirg obterAreaTricProcedimentoCirurgico(MbcAreaTricProcCirgId id) {
		return getMbcAreaTricProcCirgDAO().obterPorChavePrimaria(id);
	}

	
	@Override
	public void persistirEspecialidadeProcedCirurgico(MbcEspecialidadeProcCirgs especialidade, Boolean inserir) throws BaseException {
		getEspecialidadeProcCirgRN().persistir(especialidade, inserir);
	}

	
	@Override
	public List<MbcEspecialidadeProcCirgs> buscarEspecialidadesPeloSeqProcedimento(Integer pciSeq) {
		return getMbcEspecialidadeProcCirgsDAO().buscaEspeciadadesPeloSeqProcedimento(pciSeq);
	}

	@Override
	public MbcEspecialidadeProcCirgs obterEspecialidadeProcedimentoCirurgico(MbcEspecialidadeProcCirgsId id) {
		MbcEspecialidadeProcCirgs especialidadeProcCirgs = getMbcEspecialidadeProcCirgsDAO().obterPorChavePrimaria(id);
		if (especialidadeProcCirgs != null) {
			getMbcEspecialidadeProcCirgsDAO().initialize(especialidadeProcCirgs);
			getMbcEspecialidadeProcCirgsDAO().initialize(especialidadeProcCirgs.getEspecialidade());
			getMbcEspecialidadeProcCirgsDAO().initialize(especialidadeProcCirgs.getMbcProcedimentoCirurgicos());
		}
		return especialidadeProcCirgs;
	}

	@Override
	public MbcEspecialidadeProcCirgs obterEspecialidadeProcedimentoCirurgico(MbcEspecialidadeProcCirgsId id, Enum[] inner, Enum[] left) {
		return getMbcEspecialidadeProcCirgsDAO().obterPorChavePrimaria(id, inner, left);
	}
	
	@Override
	public List<MbcCompSangProcCirg> buscarAsscComponenteSangPeloSeqProcedimento(Integer pciSeq) {
		return getMbcCompSangProcCirgDAO().buscarAsscComponenteSangPeloSeqProcedimento(pciSeq);
	}
	
	@Override
	public MbcCompSangProcCirg buscarComponenteSanguineoEEspecialidadePorSeq(Short seq) {
		return getMbcCompSangProcCirgDAO().buscarComponenteSanguineoEEspecialidadePorSeq(seq);
	}
	
	@Override
	public List<MbcAreaTricProcCirg> buscarAreaTricPeloSeqProcedimento(Integer pciSeq) {
		return getMbcAreaTricProcCirgDAO().buscarAreaTricPeloSeqProcedimento(pciSeq);
	}
	
	protected ManterProcedimentosUsadosEquipeRN getManterProcedimentosUsadosEquipeRN() {
		return manterProcedimentosUsadosEquipeRN;
	}

	@Override
	public void validarTempoMinimoProcedimentoCirurgico(Short tempoMinimo) throws ApplicationBusinessException {
		getProcedimentoCirurgicoON().validarTempoMinimoProcedimentoCirurgico(tempoMinimo);
	}
	
	@Override
	public void validarProcedimentoHospitarInternoRelacionado(Integer numeroPHI,
			List<FatConvGrupoItemProced> convGrupoItemProcedList) throws ApplicationBusinessException {
		getProcedimentoCirurgicoON().validarProcedimentoHospitarInternoRelacionado(numeroPHI, convGrupoItemProcedList);
	}
	
	@Override
	public void validarDescricaoProcedimentoCirurgico(String descricao)
			throws ApplicationBusinessException {
		getProcedimentoCirurgicoON().validarDescricaoProcedimentoCirurgico(descricao);
	}
	
	@Override
	public void persistirProcedimentoCirurgico(MbcProcedimentoCirurgicos procedimentoCirurgico) throws BaseException {
		getProcedimentoCirurgicoON().persistirProcedimentoCirurgico(procedimentoCirurgico);
	}
	
	protected ProcedimentoCirurgicoON getProcedimentoCirurgicoON() {
		return procedimentoCirurgicoON;
	}

	@Override
	@Secure("#{s:hasPermission('equipamentoCirurgicoCadastro','executar')}")
	public void inserirMbcEquipamentoCirurgico(MbcEquipamentoCirurgico elemento) throws BaseException {
		this.getMbcEquipamentoCirurgicoON().inserir(elemento);		
	}
	@Override
	public List<MbcEquipamentoNotaSala> listarEquipamentoNotaSalaPorUnfSeqp(final short UnfSeq, final short seqp){
		return getMbcEquipamentoNotaSalaDAO().listarEquipamentoNotaSalaPorUnfSeqp(UnfSeq, seqp);	
	}	
	@Override
	@Secure("#{s:hasPermission('equipamentoCirurgicoCadastro','executar')}")
	public void atualizarMbcEquipamentoCirurgico(MbcEquipamentoCirurgico elemento) throws BaseException {
		this.getMbcEquipamentoCirurgicoON().atualizar(elemento);
	}
	
	@Override
	public MbcEquipamentoCirurgico obterMbcEquipamentoCirurgico(String descricao, Short codigo, DominioSituacao situacao) {
		return this.getMbcEquipamentoCirurgicoON().obterEquipamentoCirurgico(descricao, codigo, situacao);
	}

	@Override
	public List<MbcEquipamentoCirgPorUnid> listarEquipamentosCirgPorUnidade(MbcEquipamentoCirurgico equipamentoCirurgico) {
		return this.getMbcEquipamentoCirurgicoON().listarEquipamentosCirgPorUnidade(equipamentoCirurgico);
	}
	
	@Override
	@Secure("#{s:hasPermission('equipamentoCirurgicoCadastro','executar')}")
	public void removerEquipamentoCirurgicoPorUnid(MbcEquipamentoCirgPorUnid elemento) throws BaseException {
		this.getMbcEquipamentoCirurgicoON().removerEquipamentoCirurgicoPorUnid(elemento);
	}
	
	@Override
	@Secure("#{s:hasPermission('equipamentoCirurgicoCadastro','executar')}")
	public void inserirEquipamentoCirurgicoPorUnid(MbcEquipamentoCirgPorUnid elemento) throws BaseException {
		this.getMbcEquipamentoCirurgicoON().inserirEquipamentoCirurgicoPorUnid(elemento);
	}
	
	@Override
	@Secure("#{s:hasPermission('equipamentoCirurgicoCadastro','executar')}")
	public void atualizarEquipamentoCirurgicoPorUnid(MbcEquipamentoCirgPorUnid elemento) throws BaseException {
		this.getMbcEquipamentoCirurgicoON().atualizarEquipamentoCirurgicoPorUnid(elemento);
	}
	
	@Override
	public List<MbcAreaTricotomia> pesquisarPorSeqOuDescricao(Object parametro) {
		return getMbcAreaTricotomiaDAO().pesquisarPorSeqOuDescricao(parametro);
	}

	protected MbcEquipamentoCirurgicoON getMbcEquipamentoCirurgicoON() {
		return mbcEquipamentoCirurgicoON;
	}
	protected MbcEquipamentoNotaSalaDAO getMbcEquipamentoNotaSalaDAO(){
		return mbcEquipamentoNotaSalaDAO;
	}
		
	protected SinonimoRN getSinonimoRN() {
		return sinonimoRN;
	}
	
	protected EspecialidadeProcCirgRN getEspecialidadeProcCirgRN() {
		return especialidadeProcCirgRN;
	}

	protected ComponenteSangProcCirgRN getComponenteSangProcCirgRN() {
		return componenteSangProcCirgRN;
	}

	protected AreaTricotomiaProcCirgRN getAreaTricotomiaProcCirgRN() {
		return areaTricotomiaProcCirgRN;
	}
	
	protected MbcSinonimoProcedCirurgicoDAO getMbcSinonimoProcedCirurgicoDAO() {
		return mbcSinonimoProcedCirurgicoDAO;
	}
	
	protected MbcCompSangProcCirgDAO getMbcCompSangProcCirgDAO() {
		return mbcCompSangProcCirgDAO;
	}
	
	@Override
	@BypassInactiveModule
	public Boolean verificarCirurgiaPossuiComponenteSanguineo(Integer crgSeq) {
		return getMbcCompSangProcCirgDAO().verificarCirurgiaPossuiComponenteSanguineo(crgSeq);
	}

	@Override
	@Secure("#{s:hasPermission('destinoPaciente','pesquisar')}")
	public List<MbcDestinoPaciente> pesquisarDestinoPaciente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MbcDestinoPaciente elemento) {
		return getMbcDestinoPacienteDAO().pesquisarDestinoPaciente(firstResult, maxResult, orderProperty, asc, elemento, null);
	}
	@Override
	@Secure("#{s:hasPermission('destinoPaciente','pesquisar')}")
	public Long pesquisarDestinoPacienteCount(MbcDestinoPaciente elemento) {
		return getMbcDestinoPacienteDAO().pesquisarDestinoPacienteCount(elemento);
	}

	private MbcDestinoPacienteDAO getMbcDestinoPacienteDAO() {
		return mbcDestinoPacienteDAO;
	}
	
	@Override
	@Secure("#{s:hasPermission('destinoPaciente','executar')}")
	public void persistirDestinoPaciente(MbcDestinoPaciente destinoPaciente) throws BaseException {
		this.getMbcDestinoPacienteRN().persistirMbcDestinoPaciente(destinoPaciente);
	}
	
	private MbcDestinoPacienteRN getMbcDestinoPacienteRN() {
		return mbcDestinoPacienteRN;
	}
	protected MbcMotivoDemoraSalaRecRN getMbcMotivoDemoraSalaRecRN(){
		return mbcMotivoDemoraSalaRecRN;
	}
	
	@Override
	@Secure("#{s:hasPermission('motivoDemoraSRPACadastro','executar')}")
	public void atualizarMotivoDemoraSalaRec(MbcMotivoDemoraSalaRec motivoDemoraSalaRec) throws BaseException{
		getMbcMotivoDemoraSalaRecRN().atualizar(motivoDemoraSalaRec);
	}
	
	@Override
	@Secure("#{s:hasPermission('motivoDemoraSRPACadastro','executar')}")
	public void inserirMotivoDemoraSalaRec(MbcMotivoDemoraSalaRec motivoDemoraSalaRec) throws BaseException {
		getMbcMotivoDemoraSalaRecRN().inserir(motivoDemoraSalaRec);
	}
	
	@Override
	@Secure("#{s:hasPermission('motivoAtraso','pesquisar')}")
	public List<MbcMotivoAtraso> pesquisarMotivoAtraso(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MbcMotivoAtraso elemento) {
		return getMbcMotivoAtrasoDAO().pesquisarMotivoAtraso(firstResult, maxResult, orderProperty, asc, elemento);
	}
	@Override
	@Secure("#{s:hasPermission('motivoAtraso','pesquisar')}")
	public Long pesquisarMotivoAtrasoCount(MbcMotivoAtraso elemento) {
		return getMbcMotivoAtrasoDAO().pesquisarMotivoAtrasoCount(elemento);
	}

	private MbcMotivoAtrasoDAO getMbcMotivoAtrasoDAO() {
		return mbcMotivoAtrasoDAO;
	}
	
	@Override
	@Secure("#{s:hasPermission('motivoAtraso','executar')}")
	public void persistirMotivoAtraso(MbcMotivoAtraso motivoAtraso) throws BaseException {
		this.getMbcMotivoAtrasoRN().persistirMbcMotivoAtraso(motivoAtraso);
	}
	
	private MbcMotivoAtrasoRN getMbcMotivoAtrasoRN() {
		return mbcMotivoAtrasoRN;
	}


	private MbcEspecialidadeProcCirgsDAO getMbcEspecialidadeProcCirgsDAO() {
		return mbcEspecialidadeProcCirgsDAO;
	}

	@Override
	@Secure("#{s:hasPermission('historicoCaracteristicasSalas','pesquisar')}")
	public List<MbcMvtoCaractSalaCirg> pesquisarHistoricoAlteracoesCaractSalas(
			MbcSalaCirurgica salaCirurgicaSelecionada,
			MbcCaracteristicaSalaCirg caracteristicaSalaCirgSelecionada) {
		return getMbcMvtoCaractSalaCirgDAO().pesquisarHistoricoAlteracoesCaractSalas(salaCirurgicaSelecionada,caracteristicaSalaCirgSelecionada);
	}
	
	private MbcMvtoCaractSalaCirgDAO getMbcMvtoCaractSalaCirgDAO(){
		return mbcMvtoCaractSalaCirgDAO;
	}

	@Override
	@Secure("#{s:hasPermission('historicoCaracteristicasAlocacoesSalas','pesquisar')}")
	public List<MbcMvtoSalaEspEquipe> pesquisarHistoricoAlteracoesAlocacaoSalas(
			MbcCaractSalaEsp caractSalaEspSelecionada) {
		return getMbcMvtoSalaEspEquipeDAO().pesquisarHistoricoAlteracoesAlocacaoSalas(caractSalaEspSelecionada);
	}
	
	private MbcMvtoSalaEspEquipeDAO getMbcMvtoSalaEspEquipeDAO(){
		return mbcMvtoSalaEspEquipeDAO;
	}
	
	@Override
	public List<MbcSalaCirurgica> buscarSalaCirurgica(Short seqp,
			Short unfSeq, String nome, DominioTipoSala tipoSala,
			Boolean visivelMonitor, DominioSituacao situacao, Boolean asc, MbcSalaCirurgica.Fields ... orders) {
		return getMbcSalaCirurgicaDAO().buscarSalaCirurgica(seqp, unfSeq, nome, tipoSala, visivelMonitor, situacao, asc, orders);
	}
	
	@Override
	public Long buscarSalaCirurgicaCount(Short seqp,
			Short unfSeq, String nome, DominioTipoSala tipoSala,
			Boolean visivelMonitor, DominioSituacao situacao){
		return getMbcSalaCirurgicaDAO().buscarSalaCirurgicaCount(seqp, unfSeq, nome, tipoSala, visivelMonitor, situacao);
	}
	
	@Override
	public List<MbcSalaCirurgica> buscarSalasCirurgicas(final String filtro, final Short unfSeq, final DominioSituacao situacao){
		return getMbcSalaCirurgicaDAO().buscarSalasCirurgicas(filtro, unfSeq, situacao);
	}
	
	@Override
	public Long buscarSalasCirurgicasCount(final String filtro, final Short unfSeq, final DominioSituacao situacao){
		return getMbcSalaCirurgicaDAO().buscarSalasCirurgicasCount(filtro, unfSeq, situacao);
	}
	
	@Override
	public List<MbcCaracteristicaSalaCirg> buscarHorariosCaractPorSalaCirurgica(Short unfSeq, Short seqp) {
		return getMbcCaracteristicaSalaCirgDAO().buscarHorariosCaractPorSalaCirurgica(unfSeq, seqp);
	}
	
	private MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
		return mbcCaracteristicaSalaCirgDAO;
	}
	
	@Override
	public List<MbcCaractSalaEsp> pesquisarCaractSalaEspPorCaracteristica(Short casSeq, DominioSituacao situacao) {
		return getMbcCaractSalaEspDAO().pesquisarCaractSalaEspPorCaracteristica(casSeq, situacao);
	}
	
	@Override
	public List<VMbcProfServidor> pesquisarProfServidorMPFouMCOPorMatriculaOuNomePessoa(String parametro, Short unfSeq, Integer maxResults) {
		return getVMbcProfServidorDAO().pesquisarProfServidorMPFouMCOPorMatriculaOuNomePessoa(parametro, unfSeq, maxResults);
	}
	
	@Override
	public Long pesquisarProfServidorMPFouMCOPorMatriculaOuNomePessoaCount(String parametro, Short unfSeq) {
		return getVMbcProfServidorDAO().pesquisarProfServidorMPFouMCOPorMatriculaOuNomePessoaCount(parametro, unfSeq);
	}
	
	private VMbcProfServidorDAO getVMbcProfServidorDAO() {
		return vMbcProfServidorDAO;
	}
	
	private MbcCaractSalaEspDAO getMbcCaractSalaEspDAO() {
		return mbcCaractSalaEspDAO;
	}

	@Override
	@Secure("#{s:hasPermission('motivoCancelamentoConsulta','pesquisar')}")
	public MbcMotivoCancelamento obterMbcMotivoCancelamento(Short codigo) {
		return this.getMbcMotivoCancelamentoDAO().obterPorChavePrimaria(codigo);
	}
	
	public MbcMotivoCancelamentoDAO getMbcMotivoCancelamentoDAO() {
		return mbcMotivoCancelamentoDAO;
	}
	
	@Override
	@Secure("#{s:hasPermission('motivoCancelamentoConsulta','pesquisar')}")
	public List<MbcPerfilCancelamento> listarPerfisCancelamentos(Short mtcSeq) {
		return this.getMbcPerfilCancelamentoDAO().listarPerfisCancelamentos(mtcSeq);
	}
	
	public MbcPerfilCancelamentoDAO getMbcPerfilCancelamentoDAO() {
		return mbcPerfilCancelamentoDAO;
	}
	
	@Override
	@Secure("#{s:hasPermission('motivoCancelamentoCadastro','gravar')}")
	public void inserirMbcMotivoCancelamento(
			MbcMotivoCancelamento elemento) throws BaseException {
		this.getMbcMotivoCancelamentoRN().inserir(elemento);
	}
	
	@Override
	@Secure("#{s:hasPermission('motivoCancelamentoCadastro','gravar')}")
	public void atualizarMbcMotivoCancelamento(
			MbcMotivoCancelamento elemento) throws BaseException {
		this.getMbcMotivoCancelamentoRN().atualizar(elemento);
	}
	
	@Override
	@Secure("#{s:hasPermission('motivoCancelamentoCadastro','gravar')}")
	public void inserirMbcPerfilCancelamento(MbcPerfilCancelamento elemento) throws BaseException {
		this.getMbcPerfilCancelamentoRN().inserir(elemento);
	}
	
	@Override
	@Secure("#{s:hasPermission('motivoCancelamentoCadastro','gravar')}")
	public void removerMbcPerfilCancelamento(MbcPerfilCancelamento elemento) throws BaseException {
		this.getMbcPerfilCancelamentoRN().remover(elemento);
	}
	
	protected MbcMotivoCancelamentoRN getMbcMotivoCancelamentoRN() {
		return mbcMotivoCancelamentoRN;
	}
	
	protected MbcPerfilCancelamentoRN getMbcPerfilCancelamentoRN() {
		return mbcPerfilCancelamentoRN;
	}
	
	protected MbcAreaTricProcCirgDAO getMbcAreaTricProcCirgDAO() {
		return mbcAreaTricProcCirgDAO;
	}
	
	@Override
	@Secure("#{s:hasPermission('materialImpressaoNotaSala', 'executar')}")
	public void persistirMaterialImpNotaSalaUn(MbcMaterialImpNotaSalaUn materialNotaSala) throws BaseException {
		getMbcMaterialImpNotaSalaUnRN().persistirMbcMaterialImpNotaSalaUn(materialNotaSala);
	}
	
	@Override
	@Secure("#{s:hasPermission('materialImpressaoNotaSala', 'executar')}")
	public void removerMaterialImpNotaSalaUn(MbcMaterialImpNotaSalaUn materialNotaSala) throws BaseException {
		getMbcMaterialImpNotaSalaUnRN().removerMbcMaterialImpNotaSalaUn(materialNotaSala);
	}
	
	private MbcMaterialImpNotaSalaUnRN getMbcMaterialImpNotaSalaUnRN() {
		return mbcMaterialImpNotaSalaUnRN;
	}
	
	@Override
	public void validarItemMaterialNotaSala(MbcMaterialImpNotaSalaUn materialNotaSala) throws BaseException {
		getMateriaisImpressaoNotaSalaON().validarItemMaterialNotaSala(materialNotaSala);
	}
	
	@Override
	@Secure("#{s:hasPermission('materialImpressaoNotaSala', 'pesquisar')}")
	public List<CadastroMateriaisImpressaoNotaSalaVO> pesquisarCadastroMateriaisImpressaoNotaSala(MbcUnidadeNotaSala unidadeNotaSala) {
		return  getMateriaisImpressaoNotaSalaON().pesquisarCadastroMateriaisImpressaoNotaSala(unidadeNotaSala);
	}
	
	@Override
	public List<ScoMaterial> pesquisarMateriaisAtivosGrupoMaterial(Object objPesquisa) throws ApplicationBusinessException {
		return getMateriaisImpressaoNotaSalaON().pesquisarMateriaisAtivosGrupoMaterial(objPesquisa);
	}

	@Override
	public Long pesquisarMateriaisAtivosGrupoMaterialCount(Object objPesquisa) throws ApplicationBusinessException {
		return getMateriaisImpressaoNotaSalaON().pesquisarMateriaisAtivosGrupoMaterialCount(objPesquisa);
	}

	private MateriaisImpressaoNotaSalaON getMateriaisImpressaoNotaSalaON() {
		return materiaisImpressaoNotaSalaON;
	}
	
	@Override
	public MbcMaterialImpNotaSalaUn obterMaterialImpNotaSalaUn(Integer seq) {
		return getMbcMaterialImpNotaSalaUnDAO().obterPorChavePrimaria(seq);
	}
	
	private MbcMaterialImpNotaSalaUnDAO getMbcMaterialImpNotaSalaUnDAO() {
		return mbcMaterialImpNotaSalaUnDAO;
	}
	
	@Override
	public MbcUnidadeNotaSala obterUnidadeNotaSalaPorChavePrimaria(Object id) {
		return getMbcUnidadeNotaSalaDAO().obterPorChavePrimaria(id);
	}
	
	@Override
	public MbcUnidadeNotaSala obterUnidadeNotaSalaPorChavePrimaria(Object id, Enum[] inner, Enum[] left) {
		return getMbcUnidadeNotaSalaDAO().obterPorChavePrimaria(id, inner, left);
	}
	
	@Override
	public List<MbcHorarioTurnoCirg> buscarTurnosPorUnidadeFuncionalSb(Object param, Short unfSeq) {
		return getMbcHorarioTurnoCirgDAO().buscarTurnosPorUnidadeFuncionalSb(param, unfSeq);
	}
	@Override
	public Long buscarTurnosPorUnidadeFuncionalSbCount(Object param, Short unfSeq) {
		return getMbcHorarioTurnoCirgDAO().buscarTurnosPorUnidadeFuncionalSbCount(param, unfSeq);
	}

	@Override
	public MbcCaracteristicaSalaCirg obterCaracteristicaSalaCirgPorPK(Short seq) {
		return getMbcCaracteristicaSalaCirgDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	public MbcCaractSalaEsp obterCaractSalaEspPorPK(MbcCaractSalaEspId id) {
		return getMbcCaractSalaEspDAO().obterPorChavePrimaria(id);
	}

	@Override
	@Secure("#{s:hasPermission('mapearSalas', 'persistir')}")
	public void gravarMbcCaracteristicaSalaCirg(MbcCaracteristicaSalaCirg caracteristicaSalaCirg) throws BaseException {
		getMapeamentoSalasON().gravarMbcCaracteristicaSalaCirg(caracteristicaSalaCirg);
	}
	
	@Override
	@Secure("#{s:hasPermission('mapearSalas', 'persistir')}")
	public void gravarMbcCaractSalaEsp(MbcCaractSalaEsp caractSalaEsp) throws BaseException {
		getMapeamentoSalasON().gravarMbcCaractSalaEsp(caractSalaEsp);
	}
	
	@Override
	public MbcProfAtuaUnidCirgs obterMbcProfAtuaUnidCirgsPorId(MbcProfAtuaUnidCirgsId id) {
		return getMbcProfAtuaUnidCirgsDAO().obterPorChavePrimaria(id);
	}

	private MapeamentoSalasON getMapeamentoSalasON() {
		return mapeamentoSalasON;
	}
	
	@Override
	@Secure("#{s:hasPermission('questaoMotivoCancelamentoCadastro','executar')}")
	public void inserirMbcQuestao(MbcQuestao elemento) throws BaseException {
		this.getMbcQuestaoRN().inserir(elemento);
	}
	
	@Override
	@Secure("#{s:hasPermission('questaoMotivoCancelamentoCadastro','executar')}")
	public void atualizarMbcQuestao(MbcQuestao elemento) throws BaseException {
		this.getMbcQuestaoRN().atualizar(elemento);
	}
	
	private MbcQuestaoRN getMbcQuestaoRN() {
		return mbcQuestaoRN;
	}

	@Override
	@Secure("#{s:hasPermission('questaoMotivoCancelamentoCadastro','executar')}")
	public void atualizarMbcValorValidoCanc(MbcValorValidoCanc elemento) throws BaseException {
		this.getMbcValorValidoCancRN().atualizar(elemento);
	}
	
	@Override
	@Secure("#{s:hasPermission('questaoMotivoCancelamentoCadastro','executar')}")
	public void inserirMbcValorValidoCanc(MbcValorValidoCanc elemento) throws BaseException {
		this.getMbcValorValidoCancRN().inserir(elemento);
	}
	
	private MbcValorValidoCancRN getMbcValorValidoCancRN() {
		return mbcValorValidoCancRN;
	}
	
	@Override
	public List<MbcTipoSala> pesquisarTipoSalas(Short seq, String descricao, DominioSituacao situacao) {
		return getMbcTipoSalaDAO().pesquisarTipoSalas(seq, descricao, situacao);
	}
	
	@Override
	public List<MbcTipoSala> buscarTipoSalaAtivasPorCodigoOuDescricao(Object objPesquisa) {
		return getMbcTipoSalaDAO().buscarTipoSalaAtivasPorCodigoOuDescricao(objPesquisa);
	}
	
	@Override
	public Long contarTipoSalaAtivasPorCodigoOuDescricao(Object objPesquisa) {
		return getMbcTipoSalaDAO().contarTipoSalaAtivasPorCodigoOuDescricao(objPesquisa);
	}

	@Override
	public MbcTurnos obterMbcTurnodById(final String valor) {
		return getMbcTurnosDAO().obterPorChavePrimaria(valor);
	}

	@Override
	public List<MbcTurnos> pesquisarTiposTurno(Object objPesquisa) {
		return getMbcTurnosRN().pesquisarTiposTurno(objPesquisa);
	}

	@Override
	public Long pesquisarTiposTurnoCount(Object objPesquisa) {
		return getMbcTurnosRN().pesquisarTiposTurnoCount(objPesquisa);
	}
	
	private MbcTipoSalaDAO getMbcTipoSalaDAO() {
		return mbcTipoSalaDAO;
	}
	
	@Override
	@Secure("#{s:hasPermission('tipoSalas', 'persistir')}")
	public void gravarMbcTipoSala(MbcTipoSala tpSala) throws ApplicationBusinessException {
		getMbcTipoSalaON().gravarMbcTipoSala(tpSala);
	}
	
	private MbcTipoSalaON getMbcTipoSalaON() {
		return mbcTipoSalaON;
	}
	
	@Override
	@Secure("#{s:hasPermission('manterTipoTurno', 'persistir')}")
	public void persistirMbcTurnos(MbcTurnos turno) throws ApplicationBusinessException {
		getMbcTurnosRN().persistirMbcTurnos(turno);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterTipoTurno', 'persistir')}")
	public void atualizarMbcTurnos(MbcTurnos turno) throws ApplicationBusinessException {
		getMbcTurnosRN().atualizarMbcTurnos(turno);
	}
	
	public MbcTurnosRN getMbcTurnosRN(){
		return mbcTurnosRN;
	}
	
	public MbcTurnosDAO getMbcTurnosDAO(){
		return mbcTurnosDAO;
	}

	@Override
	public List<MbcTurnos> pesquisarTiposTurnoPorFiltro(MbcTurnos turnoFiltro) {
		return getMbcTurnosDAO().pesquisarTiposTurnoPorFiltro(turnoFiltro);
	}
	
	@Override
	public List<LinhaReportVO> pesquisarEspecialidadePorTipoProcCirgs(String strPesquisa) {
		return getMbcEspecialidadeProcCirgsDAO().pesquisarEspecialidadePorTipoProcCirgs(strPesquisa);
	}

	@Override
	public Long pesquisarEspecialidadePorTipoProcCirgsCount(String strPesquisa) {
		return getMbcEspecialidadeProcCirgsDAO().pesquisarEspecialidadePorTipoProcCirgsCount(strPesquisa);
	}
	
	private ProcedimentoDiagnosticoTerapeuticoON getProcedimentoDiagnosticoTerapeuticoON() {
		return procedimentoDiagnosticoTerapeuticoON;
	}

	@Override
	public Long pesquisarProcDiagTerapCount(Integer seq, String descricao,
			Short especialidade, DominioIndContaminacao contaminacao) {
		return getProcedimentoDiagnosticoTerapeuticoON().pesquisarProcDiagTerapCount(seq, descricao, especialidade, contaminacao);
	}

	@Override
	public List<LinhaReportVO> pesquisarProcDiagTerap(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer seq,
			String descricao, Short especialidade, DominioIndContaminacao contaminacao) {
		return getProcedimentoDiagnosticoTerapeuticoON().pesquisarProcDiagTerap(firstResult, maxResult, orderProperty, asc, seq, descricao, especialidade, contaminacao);
	}

	@Override
	public String persistirPdtEquipamento(PdtEquipamento equipamento
			) throws ApplicationBusinessException {
		return getEquipamentosDiagnosticoTerapeuticoON().persistirPdtEquipamento(equipamento);
	}

	@Override
	public String removerPdtEquipPorProc(PdtEquipPorProc equipPorProc) {
		return getEquipamentosDiagnosticoTerapeuticoON().removerPdtEquipPorProc(equipPorProc);	
	}

	@Override
	public String removerPdtInstrPorEquip(PdtInstrPorEquip instrPorEquip) {
		return getEquipamentosDiagnosticoTerapeuticoON().removerPdtInstrPorEquip(instrPorEquip);
	}

	@Override
	public String persistirPdtEquipPorProc(PdtEquipamento equipamento,
			PdtProcDiagTerap procDiagTerap) throws ApplicationBusinessException {
		return getEquipamentosDiagnosticoTerapeuticoON().persistirPdtEquipPorProc(equipamento, procDiagTerap);
	}

	@Override
	public String persistirPdtInstrPorEquip(PdtEquipamento equipamento,
			PdtInstrumental instrumental) throws ApplicationBusinessException {
		return getEquipamentosDiagnosticoTerapeuticoON().persistirPdtInstrPorEquip(equipamento, instrumental);
	}
	
	private EquipamentosDiagnosticoTerapeuticoON getEquipamentosDiagnosticoTerapeuticoON() {
		return equipamentosDiagnosticoTerapeuticoON;
	}
	
	private PdtInstrumentalON getInstrumentosON() {
		return pdtInstrumentalON;
	}

	@Override
	public String persistirPdtInstrumental(PdtInstrumental instrumental) throws ApplicationBusinessException {
		return getInstrumentosON().persistirPdtInstrumental(instrumental);
	}
	
	@Override
	public String persistirPdtInstrPorEquip(PdtInstrPorEquip instrPorEquip) throws ApplicationBusinessException{
		return getInstrumentosON().persistirPdtInstrPorEquip(instrPorEquip);
	}
	
	@Override
	public String removerPdtInstrumental(PdtInstrumental instrumental){
		return getInstrumentosON().removerPdtInstrumental(instrumental);
	}

	@Override
	public PdtInstrumental obterPdtInstrumentalPorSeq(Integer seq) {
		return getPdtInstrumentalDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public Long listarPdtInstrumentalAtivaPorDescricaoCount(Object strPesquisa){
		return getPdtInstrumentalDAO().listarPdtInstrumentalAtivaPorDescricaoCount(strPesquisa);
	}

	@Override
	public List<PdtInstrumental> listarPdtInstrumentalAtivaPorDescricao(Object strPesquisa) {
		return getPdtInstrumentalDAO().listarPdtInstrumentalAtivaPorDescricao(strPesquisa);
	}

	@Override
	public List<PdtInstrumental> pesquisarPdtInstrumental(final String strPesquisa, final Short deqSeq){
		return getPdtInstrumentalDAO().pesquisarPdtInstrumental(strPesquisa, deqSeq);
	}

	@Override
	public Long pesquisarPdtInstrumentalCount(final String strPesquisa, final Short deqSeq) {
		return getPdtInstrumentalDAO().pesquisarPdtInstrumentalCount(strPesquisa, deqSeq);
	}
	
	@Override
	public List<PdtInstrumental> listarPdtInstrumentalPorSeqDescricaoSituacao(Integer seq, String descricao, DominioSituacao situacao, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return getPdtInstrumentalDAO().listarPdtInstrumentalPorSeqDescricaoSituacao(seq, descricao, situacao, firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public Long listarPdtInstrumentalPorSeqDescricaoSituacaoCount(Integer seq, String descricao, DominioSituacao situacao) {
		return getPdtInstrumentalDAO().listarPdtInstrumentalPorSeqDescricaoSituacaoCount(seq, descricao, situacao);
	}


	
	@Override
	public void refreshPdtEquipPorProc(List<PdtEquipPorProc> listPdtEquipPorProc) {
		getEquipamentosDiagnosticoTerapeuticoON().refreshPdtEquipPorProc(listPdtEquipPorProc);
	}
	
	@Override
	public void refreshPdtInstrPorEquip(List<PdtInstrPorEquip> listInstrPorEquip) {
		getEquipamentosDiagnosticoTerapeuticoON().refreshPdtInstrPorEquip(listInstrPorEquip);
	}
	
	@Override
	public List<PdtInstrPorEquip> listarPdtInstrPorEquip(Integer seqInstrumento) {
		return getPdtInstrPorEquipDAO().listarPdtInstrPorEquip(seqInstrumento);
	}

	@Override
	public List<PdtInstrPorEquip> listarPdtInstrPorEquipAtivoPorEquip(Short seq) {
		return getPdtInstrPorEquipDAO().listarPdtInstrPorEquipAtivoPorEquip(seq);
	}

	@Override
	public PdtInstrPorEquip obterPdtInstrPorEquipPorId(PdtInstrPorEquipId id) {
		return getPdtInstrPorEquipDAO().obterPorChavePrimaria(id);
	}
	
	@Override
	public void atualizarServidor() throws ApplicationBusinessException {
		getInstrumentosRN().atualizarServidor();
	}
	
	@Override
	public List<MbcNecessidadeCirurgica> buscarNecessidadeCirurgicaPorCodigoOuDescricao(String parametro, boolean somenteAtivos) {
		return getMbcNecessidadeCirurgicaDAO().buscarNecessidadeCirurgicaPorCodigoOuDescricao(parametro, somenteAtivos);
	}
	
	@Override
	public Long countBuscarNecessidadeCirurgicaPorCodigoOuDescricao(String parametro, boolean somenteAtivos) {
		return getMbcNecessidadeCirurgicaDAO().countBuscarNecessidadeCirurgicaPorCodigoOuDescricao(parametro, somenteAtivos);
	}
	
	private PdtInstrumentalRN getInstrumentosRN() {
		return pdtInstrumentalRN;
	}
	
	public List<MbcProfAtuaUnidCirgs> pesquisarProfissionaisUnidCirgAtivoPorServidorUnfSeq(final RapServidores servidor, final Short unf_Seq) {
		return getMbcProfAtuaUnidCirgsDAO().pesquisarProfissionaisUnidCirgAtivoPorServidorUnfSeq(servidor, unf_Seq);
	}
	
	@Override
	public MbcProfAtuaUnidCirgs pesquisarProfUnidCirgAtivoPorServidorUnfSeqFuncao(final RapServidores servidor, final Short unf_Seq, 
			final DominioFuncaoProfissional funcao) {
		return getMbcProfAtuaUnidCirgsDAO().pesquisarProfUnidCirgAtivoPorServidorUnfSeqFuncao(servidor, unf_Seq, funcao);

	}
	
	@Override
	public List<MbcProfAtuaUnidCirgs> pesquisarProfissionaisUnidCirgAtivoPorServidorUnfSeq(RapServidores servidor, Short unf_Seq, boolean considerarFuncoes) {
		return getMbcProfAtuaUnidCirgsDAO().pesquisarProfissionaisUnidCirgAtivoPorServidorUnfSeq(servidor, unf_Seq, considerarFuncoes);

	}

	@Override
	public String persistirPdtDescPadrao(PdtDescPadrao descricaoPadrao)
			throws ApplicationBusinessException {
		return getPdtDescricaoPadraoON().persistirPdtDescPadrao(descricaoPadrao);
	}
	@Override
	public String excluirPdtDescPadrao(PdtDescPadrao descricaoPadrao) {
		return getPdtDescricaoPadraoON().removerPdtDescPadrao(descricaoPadrao);
	}
	
	private PdtDescricaoPadraoON getPdtDescricaoPadraoON() {
		return pdtDescricaoPadraoON;
	}
	@Override
	public List<LinhaReportVO> listarEspecialidadePorNomeOuSigla(
			String parametro) {
		return getMbcCaractSalaEspDAO().listarEspecialidadePorNomeOuSigla(parametro);
	}
	@Override
	public Long listarEspecialidadePorNomeOuSiglaCount(String parametro) {
		return getMbcCaractSalaEspDAO().listarEspecialidadePorNomeOuSiglaCount(parametro);
	}
	
	protected PdtInstrPorEquipDAO getPdtInstrPorEquipDAO() {
		return pdtInstrPorEquipDAO;
	}

	protected PdtInstrumentalDAO getPdtInstrumentalDAO() {
		return pdtInstrumentalDAO;
	}
	
	
	@Override
	public Long pesquisarEspecialidadeEquipeSalaCount(MbcSalaCirurgica sala, 
			MbcProfAtuaUnidCirgs profAtuaUnidcirgs, Date date) {
		return getMbcSalaCirurgicaDAO().pesquisarEspecialidadeEquipeCount(sala, profAtuaUnidcirgs, date);
	}
	
	@Override
	public List<MbcSalaCirurgica> pesquisarSalasCirurgicasPorUnfSeqSeqpOuNome(Object objSalaCirurgica, AghUnidadesFuncionais unidadeFuncional){
		return getMbcSalaCirurgicaDAO().pesquisarSalasCirurgicasPorUnfSeqSeqpOuNome(objSalaCirurgica, unidadeFuncional);
	}
	
	@Override
	public Long pesquisarSalasCirurgicasPorUnfSeqSeqpOuNomeCount(Object objSalaCirurgica, AghUnidadesFuncionais unidadeFuncional){
		return getMbcSalaCirurgicaDAO().pesquisarSalasCirurgicasPorUnfSeqSeqpOuNomeCount(objSalaCirurgica, unidadeFuncional);
	}

	
	@Override
	public List<MbcCaracteristicaSalaCirg> pesquisarSalaDiaSemanaTurno(Object objSalaDiaSemanaTurno, AghUnidadesFuncionais unidadeFuncional){
		return getMbcCaracteristicaSalaCirgDAO().pesquisarSalaDiaSemanaTurno(objSalaDiaSemanaTurno, unidadeFuncional);
	}
	
	@Override
	public Long pesquisarSalaDiaSemanaTurnoCount(Object objSalaDiaSemanaTurno, AghUnidadesFuncionais unidadeFuncional){
		return getMbcCaracteristicaSalaCirgDAO().pesquisarSalaDiaSemanaTurnoCount(objSalaDiaSemanaTurno, unidadeFuncional);
	}

	
	@Override
	public String excluirEscalaProfissionaisPorSala(MbcEscalaProfUnidCirg escalaExclusao) {
		MbcEscalaProfUnidCirg mbcEscalaProfUnidCirgOriginal = getMbcEscalaProfUnidCirgDAO().obterPorChavePrimaria(escalaExclusao.getId());
		getMbcEscalaProfUnidCirgDAO().remover(mbcEscalaProfUnidCirgOriginal);
		return "MENSAGEM_EXCLUSAO_ESCALA_PROFISSIONAL";
	}
	
	protected MbcEscalaProfUnidCirgRN getMbcEscalaProfUnidCirgRN() {
		return mbcEscalaProfUnidCirgRN;
	}
	
	protected MbcGrupoAlcadaRN2 getMbcGrupoAlcadaRN2() {
		return mbcGrupoAlcadaRN2;
	}
	
	@Override
	public void inserirMbcEscalaProfUnidCirg(MbcEscalaProfUnidCirg novaEscala) throws ApplicationBusinessException{
		getMbcEscalaProfUnidCirgRN().insert(novaEscala);
	}
	
	@Override
	public void deletarMbcEscalaProfUnidCirg(MbcEscalaProfUnidCirg editarEscala){		
		getMbcEscalaProfUnidCirgRN().delete(editarEscala);
	}
	
	@Override
	public List<MbcEscalaProfUnidCirg> pesquisarProfissionaisEscalaPorSala(AghUnidadesFuncionais unidadeFuncional,
																			MbcSalaCirurgica salaCirurgica,
																			DominioDiaSemana diaSemana, MbcTurnos turnos,
																			DominioFuncaoProfissional funcaoProfissional,
																			RapServidores profissionalServ, Integer firstResult,
																			Integer maxResult, String orderProperty, boolean asc) {

			return getMbcEscalaProfUnidCirgDAO().pesquisarProfissionaisEscalaPorSala(unidadeFuncional, salaCirurgica, diaSemana, 
																						turnos, funcaoProfissional, profissionalServ, 
																						firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long pesquisarProfissionaisEscalaPorSalaCount(AghUnidadesFuncionais unidadeFuncional,
															MbcSalaCirurgica salaCirurgica,
															DominioDiaSemana diaSemana, MbcTurnos turnos,
															DominioFuncaoProfissional funcaoProfissional,
															RapServidores profissionalServ){
			
			return getMbcEscalaProfUnidCirgDAO().pesquisarProfissionaisEscalaPorSalaCount(unidadeFuncional, salaCirurgica, diaSemana, 
																						  turnos, funcaoProfissional, profissionalServ);
	}
	
	@Override
	public List<MbcProfAtuaUnidCirgs> pesquisarFuncaoProfissionalEscala(Object objFuncaoProfissional, AghUnidadesFuncionais unidadeFuncional){
			return getMbcProfAtuaUnidCirgsDAO().pesquisarFuncaoProfissionalEscala(objFuncaoProfissional, unidadeFuncional);
	}
	
	protected RelatorioEscalaProfissionaisSemanaON getRelatorioEscalaProfissionaisSemanaON() {
		return relatorioEscalaProfissionaisSemanaON;
	}
	
	@Override
	public String pesquisarUnidadeTurno(Short unidadeFuncional, String turno){
		return getRelatorioEscalaProfissionaisSemanaON().buscarUnidadeCirurgicaTurno(unidadeFuncional, turno);
	}
	
	@Override
	public List<RelatorioEscalaProfissionaisSemanaVO> buscarDadosRelatorioEscalaProfissionaisSemana(AghUnidadesFuncionais unidadeFuncional,
																								 	MbcTurnos turnos,
																									DominioFuncaoProfissional funcaoProfissional1,
																									DominioFuncaoProfissional funcaoProfissional2,
																									DominioFuncaoProfissional funcaoProfissional3,
																									DominioFuncaoProfissional funcaoProfissional4) throws ApplicationBusinessException{
		
		return getRelatorioEscalaProfissionaisSemanaON().buscarDadosRelatorioEscalaProfissionaisSemana(unidadeFuncional, turnos, 
																										funcaoProfissional1, 
																										funcaoProfissional2, 
																										funcaoProfissional3, 
																										funcaoProfissional4);
	}
	
	protected RelatorioResumoCirurgiasRealizadasPorPeriodoON getRelatorioResumoCirurgiasRealizadasPorPeriodoON() {
		return relatorioResumoCirurgiasRealizadasPorPeriodoON;
	}
	
	@Override
	public RelatorioResumoCirurgiasRealizadasPorPeriodoVO buscaDadosRelatorio(AghUnidadesFuncionais unidadeCirurgica, Date dataInicial, Date dataFinal) throws ApplicationBusinessException {
		return getRelatorioResumoCirurgiasRealizadasPorPeriodoON().buscaDadosRelatorio(unidadeCirurgica, dataInicial, dataFinal);
	}
	
	@Override
	public List<RelatorioResumoCirurgiasRealizadasPorPeriodoListVO> buscaDadosRelatorioDetalhe(AghUnidadesFuncionais unidadeCirurgica, Date dataInicial, Date dataFinal) throws ApplicationBusinessException {
		return getRelatorioResumoCirurgiasRealizadasPorPeriodoON().buscaDadosRelatorioDetalhe(unidadeCirurgica, dataInicial, dataFinal);
	}
	@Override
	public List<DominioFuncaoProfissional> listarDominioFuncaoPorMedico() {
		return getMbcEscalaProfUnidCirgRN().listarFuncaoProfissional();
	}
	@Override
	public MbcGrupoAlcadaAvalOpms persistirGrupoAlcada(MbcGrupoAlcadaAvalOpms grupoAlcada, RapServidores rapServidores) {
		return getMbcGrupoAlcadaRN2().persistirMbcGrupoAlcada(grupoAlcada, rapServidores);
	}
	@Override
	public MbcGrupoAlcadaAvalOpms buscaGrupoAlcada(DominioTipoConvenioOpms tipoConvenio, AghEspecialidades aghEspecialidades) {
		return getMbcGrupoAlcadaRN2().buscaGrupoAlcada(tipoConvenio, aghEspecialidades);
	}
	
	@Override
	public void alteraGrupoAnterior(
			MbcGrupoAlcadaAvalOpms grupoAlcadaVersaoAnterior, RapServidores rapServidores) throws ApplicationBusinessException {
		getMbcGrupoAlcadaRN2().alteraGrupoAnterior(grupoAlcadaVersaoAnterior, rapServidores);
	}
	
	@Override
	public void alterarSituacao(MbcGrupoAlcadaAvalOpms grupoAlcada, RapServidores rapServidores) {
		getMbcGrupoAlcadaRN2().alteraSituacao(grupoAlcada, rapServidores);
		
	}
 
	@Override
	public MbcAlcadaAvalOpms buscaNivelAlcada(MbcAlcadaAvalOpms nivelAlcada) throws ApplicationBusinessException {
		return getEditaNiveisAlcadaAprovacaoRN().buscaNivelAlcada(nivelAlcada);
	}
	@Override
	public void removerServidor(MbcServidorAvalOpms servidor) {
		getMbcServidorAvalOpmsRN().removerServidor(servidor);		
	}
	@Override
	public void ativarDesativarServidor(MbcServidorAvalOpms servidor) {
		getMbcServidorAvalOpmsRN().ativarDesativarServidor(servidor);
	}
	
	@Override
	public void persistirNivelAlcada(MbcAlcadaAvalOpms nivelAlcadaInsercao) throws ApplicationBusinessException{
		getPesquisaNiveisAlcadaAprovacaoRN().persistirNivelAlcada(nivelAlcadaInsercao);
		
	}
	@Override
	public void persistirNiveisAlcada(List<MbcAlcadaAvalOpms> listaAlcada) throws ApplicationBusinessException {
		getEditaNiveisAlcadaAprovacaoRN().persistirListaNiveisAlcada(listaAlcada);
	}
	private EditaNiveisAlcadaAprovacaoRN getEditaNiveisAlcadaAprovacaoRN() {
		return editaNiveisAlcadaAprovacaoRN;
	}
	@Override
	public void removerNivelAlcada(Short seqNivelExcluir) throws ApplicationBusinessException {
		getEditaNiveisAlcadaAprovacaoRN().removerNiveisAlcada(seqNivelExcluir);
	}
	@Override
	public MbcGrupoAlcadaAvalOpms buscaGrupoAlcadaPorSequencial(Short codigoGrupo) {
		return getMbcGrupoAlcadaRN2().buscaGrupoAlcadaPorSequencial(codigoGrupo);
	}
	protected PesquisaNiveisAlcadaAprovacaoRN getPesquisaNiveisAlcadaAprovacaoRN() {
		return pesquisaNiveisAlcadaAprovacaoRN;
	}
	@Override
	public List<MbcAlcadaAvalOpms> buscaNiveisAlcadaAprovacaoPorGrupo(Short seq) throws ApplicationBusinessException{
		return getPesquisaNiveisAlcadaAprovacaoRN().buscaNiveisAlcadaAprovacaoPorGrupo(seq);
	}
	@Override
	public void atualizaNivelAlacada(MbcAlcadaAvalOpms nivel){
		mbcAlcadaAvalOpmsDAO.atualizar(nivel);
	}
	@Override
	public List<MbcAlcadaAvalOpms> buscaNiveisAlcadaAprovacaoPorGrupoValor(Short seq) throws ApplicationBusinessException{
		return getPesquisaNiveisAlcadaAprovacaoRN().buscaNiveisAlcadaAprovacaoPorGrupoValor(seq);
	}
	@Override
	public List<MbcServidorAvalOpms> buscaServidoresPorNivelAlcada(MbcAlcadaAvalOpms nivelAlcada) {
		return getMbcServidorAvalOpmsRN().buscaServidoresPorNivelAlcada(nivelAlcada); 
	}
	@Override
	public MbcServidorAvalOpms buscaServidoresPorSeq(Short seq) {
		return getMbcServidorAvalOpmsRN().buscaServidoresPorSeq(seq); 
	}
	@Override
	public MbcGrupoAlcadaAvalOpms buscarGrupoAlcadaAtivo(DominioTipoConvenioOpms tipoConvenio,AghEspecialidades aghEspecialidades) {
		return getMbcGrupoAlcadaRN2().buscarGrupoAlcadaAtivo(tipoConvenio,aghEspecialidades);
	}
	@Override
	public Long buscarNotasSalaPorUnidadeCirurgicaCount(Short unfSeq) {
		return getMbcUnidadeNotaSalaDAO().obterNotaSalaPorUnidadeCount(unfSeq);
	}
	@Override
	public List<AghUnidadesFuncionais> buscarUnidadesFuncionaisCirurgiaSB(Object param) {
		return getMbcHorarioTurnoCirgDAO().buscarUnidadesFuncionaisCirurgiaSB(param);
	}
	
	@Override
	public void verificarExisteRegistroPdtDescPadrao(PdtDescPadrao descricaoPadrao) throws ApplicationBusinessException{
		getPdtDescricaoPadraoON().verificarExisteRegistro(descricaoPadrao);
	}
	@Override
	public List<HistoricoAlteracoesGrupoAlcadaVO> buscarHistoricoGrupoAlcada(
			Short seq) {
		return mbcGrupoAlcadaRN.buscarHistoricoGrupoAlcada(seq);
	}
	@Override
	public List<MbcGrupoAlcadaAvalOpms> validaGrupoEspecialidadeConvenio(MbcGrupoAlcadaAvalOpms item){
		return mbcGrupoAlcadaAvalOpmsDAO.validaGrupoEspecialidadeConvenio(item);
	}

}