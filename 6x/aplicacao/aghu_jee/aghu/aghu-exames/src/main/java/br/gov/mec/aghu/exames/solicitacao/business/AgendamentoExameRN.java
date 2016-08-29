package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNaoRestritoAreaExecutora;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.dao.AelDifAgendaPacienteDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAgendamentoMesmoHorarioVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Created by renanribeiro on 25/02/15.
 */
@Stateless
public class AgendamentoExameRN  extends BaseBusiness {

    private static final long serialVersionUID = -1928426813382232593L;
    private static final Log LOG = LogFactory.getLog(AgendamentoExameRN.class);

    @Inject
    private AghUnidadesFuncionaisDAO unidadesFuncionaisDAO;

    @Inject
    private AelSolicitacaoExameDAO solicitacaoExameDAO;

    @Inject
    private AelItemSolicitacaoExameDAO itemSolicitacaoExameDAO;

    @Inject
    private AghParametrosDAO parametrosDAO;

    @Inject
    private AghAtendimentoDAO atendimentoDAO;

    @Inject
    private AelUnfExecutaExamesDAO unfExecutaExamesDAO;

    @Inject
    private AelItemHorarioAgendadoDAO itemHorarioAgendadoDAO;

    @Inject
    private AelDifAgendaPacienteDAO difAgendaPacienteDAO;

    private static final String P_SITUACAO_A_EXECUTAR = "P_SITUACAO_A_EXECUTAR";
    private static final String P_SITUACAO_A_COLETAR = "P_SITUACAO_A_COLETAR";

    @Override
    @Deprecated
    protected Log getLogger() {
        return LOG;
    }

    public List<Short> obterListaSeqUnFExamesAgendaveis(Integer soeSeq){
    	List<Short> listaSeqUnf = new ArrayList<Short>();
    	Short unidadeFuncionalSolicitanteSeq = null;
        AelSolicitacaoExames solicitacao = getSolicitacaoExameDAO().obterAelSolicitacaoExameComAtendimentoPeloSeq(soeSeq);

        boolean situacaoExamesValida = false;
        for (AelItemSolicitacaoExames item : solicitacao.getItensSolicitacaoExame()) {
        	if(!listaSeqUnf.contains(item.getAelUnfExecutaExames().getUnidadeFuncional().getSeq())){
        		
        		if (solicitacao.getUnidadeFuncionalAreaExecutora() != null){
        			unidadeFuncionalSolicitanteSeq = solicitacao.getUnidadeFuncionalAreaExecutora().getSeq();
        		} else {
        			unidadeFuncionalSolicitanteSeq = null;
        		}
        		
	            situacaoExamesValida = validaExameAgendaInternacaoOuNaoInternacao(item.getAelUnfExecutaExames(), solicitacao.getUnidadeFuncional().getSeq(), unidadeFuncionalSolicitanteSeq, solicitacao.getAtendimento());
	            
	            if (situacaoExamesValida) {
	            	listaSeqUnf.add(item.getAelUnfExecutaExames().getUnidadeFuncional().getSeq());
	            	situacaoExamesValida = false;
	            }
        	}        	
        }
        
       return listaSeqUnf; 
    }
    
    
    public boolean validaAgendamentoExame(Integer atendimentoSeq, Integer solicitacaoExamesSeq, Integer seqAtendimentoDiverso,
                                          Short unidadeFuncionalSolicitanteSeq, Short unidadeFuncionalUsuarioSeq) {
        AelSolicitacaoExames solicitacao = getSolicitacaoExameDAO().obterPeloId(solicitacaoExamesSeq);

        solicitacao.setItensSolicitacaoExame(getItemSolicitacaoExameDAO().pesquisarItemSolicitacaoExamePorSolicitacaoExame(solicitacaoExamesSeq));

        AghAtendimentos atendimento = getAtendimentoDAO().buscarAtendimentoPorSeq(atendimentoSeq);
        
        if (verificaAtendimentoDiverso(atendimento.getSeq(), seqAtendimentoDiverso)
        		|| !validaUnidadeExecutoraIdentificada(unidadeFuncionalUsuarioSeq)
                || verificaSolicitantePacienteCaracteristica(unidadeFuncionalSolicitanteSeq, ConstanteAghCaractUnidFuncionais.SMO)
                || verificaSolicitantePacienteCaracteristica(unidadeFuncionalSolicitanteSeq, ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)
                ) {
            return false;
        }

        boolean itemAgendavel = false;
        for (AelItemSolicitacaoExames item : solicitacao.getItensSolicitacaoExame()) {
        	itemAgendavel = validaSituacaoExame(item) && validaExameAgendaInternacaoOuNaoInternacao(item.getAelUnfExecutaExames(), unidadeFuncionalSolicitanteSeq, unidadeFuncionalUsuarioSeq, atendimento);
            if (itemAgendavel) {
                break;
            }
        }

        return itemAgendavel;
    }

    /**
     * Para atendimento diversos não haverá agendamento
     * */
    public boolean verificaAtendimentoDiverso(Integer seqAtendimento, Integer seqAtendimentoDiverso) {
        return seqAtendimento == null && seqAtendimentoDiverso != null;
    }

    /**
     * Se for solicitante e paciente da emergência ou SMO não chama agendamento
     * */
    public boolean verificaSolicitantePacienteCaracteristica(Short unidadeFuncionalSolicitanteSeq,
                                                              ConstanteAghCaractUnidFuncionais caracteristicaUnidadeFuncional) {
        ConstanteAghCaractUnidFuncionais[] caracts = new ConstanteAghCaractUnidFuncionais[] {caracteristicaUnidadeFuncional};
        return getUnidadesFuncionaisDAO().unidadeFuncionalPossuiCaracteristica(unidadeFuncionalSolicitanteSeq , caracts);
    }

    /**
     *  Retorna os horarios agendados para exames do mesmo grupo e que permitam agendamento concorrente.
     * */
    public List<ExameAgendamentoMesmoHorarioVO> pesquisaHorariosAgendamentoMesmoGrupoExames(AghAtendimentos atendimento,
                                                    AelItemSolicitacaoExames itemSolicitacaoExames) {
        return getItemHorarioAgendadoDAO().pesquisaHorariosAgendamentoMesmoGrupoExames(atendimento, itemSolicitacaoExames,
                DominioSituacaoHorario.M);
    }

    public boolean validaSituacaoExame(AelItemSolicitacaoExames itemSolicitacaoExames) {
        AghParametros paramtroAColetar = getParametrosDAO().obterAghParametroPorNome(P_SITUACAO_A_COLETAR);
        AghParametros paramtroAExecutar = getParametrosDAO().obterAghParametroPorNome(P_SITUACAO_A_EXECUTAR);

        return (itemSolicitacaoExames.getSituacaoItemSolicitacao().getCodigo().equals(paramtroAColetar.getVlrTexto())
                || itemSolicitacaoExames.getSituacaoItemSolicitacao().getCodigo().equals(paramtroAExecutar.getVlrTexto()));

    }

    /**
     * Valida se exame pode ser agendado para paciente internado ou não internado, outras condições devem ser preenchidas
     * para validar esse agendamento.
     * */
    public boolean validaExameAgendaInternacaoOuNaoInternacao(AelUnfExecutaExames unfExecutoraItemExame, Short unidadeFuncionalSolicitanteSeq, Short unidadeFuncionalUsuarioSeq, AghAtendimentos atendimento) {
        //AelUnfExecutaExames unfExecutaExames = getUnfExecutaExamesDAO().buscaAelUnfExecutaExames(emaExaSigla, materialSeq, unfSeq);

        DominioSimNaoRestritoAreaExecutora agendamPrevioInternacao = unfExecutoraItemExame != null ? unfExecutoraItemExame.getIndAgendamPrevioInt() : DominioSimNaoRestritoAreaExecutora.N;
        DominioSimNaoRestritoAreaExecutora agendamPrevioNaoInternacao = unfExecutoraItemExame != null ? unfExecutoraItemExame.getIndAgendamPrevioNaoInt() : DominioSimNaoRestritoAreaExecutora.N;

        boolean origemInternacao = validaDominioOrigemAtendimentoContidoEm(atendimento.getOrigem(),
                DominioOrigemAtendimento.I,
                DominioOrigemAtendimento.N,
                DominioOrigemAtendimento.H,
                DominioOrigemAtendimento.C);

        boolean origemNaoInternacao = validaDominioOrigemAtendimentoContidoEm(atendimento.getOrigem(),
                DominioOrigemAtendimento.A,
                DominioOrigemAtendimento.X,
                DominioOrigemAtendimento.D,
                DominioOrigemAtendimento.U);

        // Validação de exame sem restrição quanto área executora
        if (validaOrigemAtendPermissaoAgendamentoOrigem(agendamPrevioInternacao, agendamPrevioNaoInternacao, origemInternacao, origemNaoInternacao)){
            return true;
        }

        // Validação exame restrito à área executora + Unidade Executora do Usuário x Unidade Executora do exame
        if (validaRestricaoAreaExecutora(unidadeFuncionalUsuarioSeq, unfExecutoraItemExame,agendamPrevioInternacao, agendamPrevioNaoInternacao, origemInternacao, origemNaoInternacao)){
            return true;
        }

        // Validação Origem Internação x Característica da solicitação
        if (validaUnidadeExecutoraEOrigemInternacao(unidadeFuncionalUsuarioSeq, agendamPrevioInternacao, agendamPrevioNaoInternacao, origemInternacao, origemNaoInternacao, unfExecutoraItemExame)){
            return true;
        }

        return false;
    }

    private boolean validaOrigemAtendPermissaoAgendamentoOrigem(DominioSimNaoRestritoAreaExecutora agendamPrevioInternacao,
                                                                DominioSimNaoRestritoAreaExecutora agendamPrevioNaoInternacao,
                                                                boolean origemInternacao, boolean origemNaoInternacao) {
        if (((agendamPrevioInternacao.equals(DominioSimNaoRestritoAreaExecutora.S) && origemInternacao)
                || (agendamPrevioNaoInternacao.equals(DominioSimNaoRestritoAreaExecutora.S) && origemNaoInternacao))){
            return true;
        } else {
            return false;
        }
    }

    private boolean validaRestricaoAreaExecutora(Short unidadeFuncionalUsuarioSeq, AelUnfExecutaExames unfExecutoraItemExame,
                                                 DominioSimNaoRestritoAreaExecutora agendamPrevioInternacao,
                                                 DominioSimNaoRestritoAreaExecutora agendamPrevioNaoInternacao,
                                                 boolean origemInternacao, boolean origemNaoInternacao){
        if (unidadeFuncionalUsuarioSeq != null 
        		&& verificarUnidadeUsuarioPertenceHirarquiaUnfExame(unidadeFuncionalUsuarioSeq, unfExecutoraItemExame.getUnidadeFuncional().getSeq()) 
        		&& ((agendamPrevioInternacao.equals(DominioSimNaoRestritoAreaExecutora.R) && origemInternacao)
                        || (agendamPrevioNaoInternacao.equals(DominioSimNaoRestritoAreaExecutora.R) && origemNaoInternacao))){
            return true;
        } else {
            return false;
        }
    }
    
    public boolean verificarUnidadeUsuarioPertenceHirarquiaUnfExame(Short unidadeFuncionalUsuarioSeq, Short unidadeFuncionalExame){   	
    	if (unidadeFuncionalUsuarioSeq.equals(unidadeFuncionalExame)){
    		return true;
    	} else {    	
    		List<Short> hierarquiaUnF = getUnidadesFuncionaisDAO().obterUnidadesFuncionaisHierarquicasPorCaract(unidadeFuncionalExame, ConstanteAghCaractUnidFuncionais.CONTROLA_UNID_PAI);
    	
    		for (Short unf : hierarquiaUnF){
    			if (unidadeFuncionalUsuarioSeq.equals(unf)){
    				return true;	
    			}
    		}
    	}
    	
    	return false;
    }

    private boolean validaUnidadeExecutoraEOrigemInternacao(Short unidadeFuncionalUsuarioSeq,
                                                      DominioSimNaoRestritoAreaExecutora agendamPrevioInternacao,
                                                      DominioSimNaoRestritoAreaExecutora agendamPrevioNaoInternacao,
                                                      boolean origemInternacao, boolean origemNaoInternacao, AelUnfExecutaExames unfExecutoraItemExame){
        if((unidadeFuncionalUsuarioSeq != null && verificarUnidadeUsuarioPertenceHirarquiaUnfExame(unidadeFuncionalUsuarioSeq, unfExecutoraItemExame.getUnidadeFuncional().getSeq()) &&
                (verificaSolicitantePacienteCaracteristica(unidadeFuncionalUsuarioSeq, ConstanteAghCaractUnidFuncionais.UNID_COLETA)
                        || verificaSolicitantePacienteCaracteristica(unidadeFuncionalUsuarioSeq, ConstanteAghCaractUnidFuncionais.UNID_RADIOLOGIA)
                        || verificaSolicitantePacienteCaracteristica(unidadeFuncionalUsuarioSeq, ConstanteAghCaractUnidFuncionais.AREA_FECHADA))
                && ((agendamPrevioInternacao.equals(DominioSimNaoRestritoAreaExecutora.R) && origemInternacao)
                || (agendamPrevioNaoInternacao.equals(DominioSimNaoRestritoAreaExecutora.R) && origemNaoInternacao)))) {
            return true;
        } else {
            return false;
        }
    }


    private boolean validaUnidadeExecutoraIdentificada(Short unidadeFuncionalUsuarioSeq){
        if (unidadeFuncionalUsuarioSeq == null || 
        		verificaSolicitantePacienteCaracteristica(unidadeFuncionalUsuarioSeq, ConstanteAghCaractUnidFuncionais.UNID_COLETA) ||
                verificaSolicitantePacienteCaracteristica(unidadeFuncionalUsuarioSeq, ConstanteAghCaractUnidFuncionais.AREA_FECHADA)){
            return true;
        }
 
        return false;
    }


    private boolean validaDominioOrigemAtendimentoContidoEm(DominioOrigemAtendimento origemTestada,
                                                            DominioOrigemAtendimento ... origensLista) {
        for (DominioOrigemAtendimento item : origensLista) {
            if (item.equals(origemTestada)) {
                return true;
            }
        }
        return false;
    }

    public List<ExameAgendamentoMesmoHorarioVO> pesquisaHorariosDisponiveisAgendamentoConcorrente(Integer codigoPaciente,
                                                                                        String siglaExame,
                                                                                        Integer seqMaterial) {
        return getDifAgendaPacienteDAO().pesquisaHorariosDisponiveisAgendamentoConcorrente(codigoPaciente, siglaExame, seqMaterial);
    }

    public AghUnidadesFuncionaisDAO getUnidadesFuncionaisDAO() {
        return unidadesFuncionaisDAO;
    }

    public void setUnidadesFuncionaisDAO(AghUnidadesFuncionaisDAO unidadesFuncionaisDAO) {
        this.unidadesFuncionaisDAO = unidadesFuncionaisDAO;
    }

    public AelSolicitacaoExameDAO getSolicitacaoExameDAO() {
        return solicitacaoExameDAO;
    }

    public void setSolicitacaoExameDAO(AelSolicitacaoExameDAO solicitacaoExameDAO) {
        this.solicitacaoExameDAO = solicitacaoExameDAO;
    }

    public AghParametrosDAO getParametrosDAO() {
        return parametrosDAO;
    }

    public void setParametrosDAO(AghParametrosDAO parametrosDAO) {
        this.parametrosDAO = parametrosDAO;
    }

    public AelUnfExecutaExamesDAO getUnfExecutaExamesDAO() {
        return unfExecutaExamesDAO;
    }

    public void setUnfExecutaExamesDAO(AelUnfExecutaExamesDAO unfExecutaExamesDAO) {
        this.unfExecutaExamesDAO = unfExecutaExamesDAO;
    }

    public AelItemSolicitacaoExameDAO getItemSolicitacaoExameDAO() {
        return itemSolicitacaoExameDAO;
    }

    public void setItemSolicitacaoExameDAO(AelItemSolicitacaoExameDAO itemSolicitacaoExameDAO) {
        this.itemSolicitacaoExameDAO = itemSolicitacaoExameDAO;
    }

    public AghAtendimentoDAO getAtendimentoDAO() {
        return atendimentoDAO;
    }

    public void setAtendimentoDAO(AghAtendimentoDAO atendimentoDAO) {
        this.atendimentoDAO = atendimentoDAO;
    }

    public AelItemHorarioAgendadoDAO getItemHorarioAgendadoDAO() {
        return itemHorarioAgendadoDAO;
    }

    public void setItemHorarioAgendadoDAO(AelItemHorarioAgendadoDAO itemHorarioAgendadoDAO) {
        this.itemHorarioAgendadoDAO = itemHorarioAgendadoDAO;
    }

    public AelDifAgendaPacienteDAO getDifAgendaPacienteDAO() {
        return difAgendaPacienteDAO;
    }

    public void setDifAgendaPacienteDAO(AelDifAgendaPacienteDAO difAgendaPacienteDAO) {
        this.difAgendaPacienteDAO = difAgendaPacienteDAO;
    }

}
