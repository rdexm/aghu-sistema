package br.gov.mec.aghu.exames.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioUnidTempoComMinuto;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.vo.TicketExamesPacienteVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;

@Stateless
public class PesquisarRelatorioTicketExamesPacienteON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PesquisarRelatorioTicketExamesPacienteON.class);

	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private RelatorioTicketExamesPacienteON relatorioTicketExamesPacienteON;

	@Override
	protected Log getLogger() {
		return LOG;
	}

	private static final long serialVersionUID = -8960495455990825793L;
	
	public enum TickeExamenstPacienteONExceptionCode implements BusinessExceptionCode {
		P_COD_UNIDADE_COLETA_DEFAULT_NÃO_É_UMA_UND_FUNCIONAL_VÁLIDA;
	}

	public List<TicketExamesPacienteVO> pesquisarRelatorioTicketExamesPaciente(Integer codSolicitacao, Short unfSeq, Set<Short> listaUnfSeq)
			throws ApplicationBusinessException {

		List<AelItemSolicitacaoExames> solicitacoes = getAelSolicitacaoExameDAO().pesquisarRelatorioTicketExamesPaciente(codSolicitacao,
				true, unfSeq, listaUnfSeq);

		List<TicketExamesPacienteVO> tickets = new ArrayList<TicketExamesPacienteVO>();

		for (AelItemSolicitacaoExames itemSolicitacao : solicitacoes) {
			TicketExamesPacienteVO vo = geraTicketExamesPacienteVO(itemSolicitacao);
			if(vo != null) {
				tickets.add(vo);
			}
		}

		getRelatorioTicketExamesPacienteON().verificarSetarMaiorTempoRealizacao(tickets);

		getRelatorioTicketExamesPacienteON().organizarPorMaterialComparece(tickets);
		
		getRelatorioTicketExamesPacienteON().verificarSetarMaiorTempoJejum(tickets);

        // Itens devem ser agrupados e ordenados pela descrição do material de análise
//		Collections.sort(tickets);

		// converteParaXml(tickets);
		return tickets;

	}
	
	@SuppressWarnings("PMD.NPathComplexity")
	public TicketExamesPacienteVO geraTicketExamesPacienteVO(AelItemSolicitacaoExames itemSolicitacao) throws ApplicationBusinessException{
		TicketExamesPacienteVO vo = new TicketExamesPacienteVO();
		AelSolicitacaoExames solicitacaoExames = itemSolicitacao.getSolicitacaoExame();
		AelUnfExecutaExames executaExames = itemSolicitacao.getAelUnfExecutaExames();
		AelExamesMaterialAnalise examesMaterialAnalise = executaExames.getAelExamesMaterialAnalise();
		AelExames aelExames = examesMaterialAnalise.getAelExames();
		AelMateriaisAnalises materialAnalise = examesMaterialAnalise.getAelMateriaisAnalises();
		// AghCaractUnidFuncionais caractUnidFuncionais =
		// executaExames.getAghCaractUnidFuncionais();
		AghAtendimentos atendimento = solicitacaoExames.getAtendimento();
		Short maiorTempoJejum = null;
		if (atendimento.getSeq() != null){
			Enum[] leftJoin = {AghAtendimentos.Fields.CONSULTA, AghAtendimentos.Fields.PACIENTE
					, AghAtendimentos.Fields.ESPECIALIDADE, AghAtendimentos.Fields.UNIDADE_FUNCIONAL
					,AghAtendimentos.Fields.SOLICITACAO_EXAMES, AghAtendimentos.Fields.INTERNACAO};
			atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(atendimento.getSeq(), null, leftJoin);
		}
		
		AelAtendimentoDiversos atendimentoDiversos = solicitacaoExames.getAtendimentoDiverso();
		FatConvenioSaudePlano convenioSaudePlano = solicitacaoExames.getConvenioSaudePlano();
		RapServidores servidorResponsavel = solicitacaoExames.getServidorResponsabilidade();

		AghCaractUnidFuncionais caractUnidFuncionais =
		// getAghCaractUnidFuncionaisDAO().buscarCaracteristicaPorUnidadeCaracteristica(executaExames.getId().getUnfSeq().getSeq(),ConstanteAghCaractUnidFuncionais.AREA_FECHADA);
		this.getAghuFacade().buscarCaracteristicaPorUnidadeCaracteristica(executaExames.getId().getUnfSeq().getSeq(),
				ConstanteAghCaractUnidFuncionais.AREA_FECHADA);

		if (caractUnidFuncionais != null
				&& !caractUnidFuncionais.getId().getCaracteristica().equals(ConstanteAghCaractUnidFuncionais.AREA_FECHADA)) {
			caractUnidFuncionais = null;
		}

		DominioOrigemAtendimento dominioOrigemAtendimento = getRelatorioTicketExamesPacienteON().getExamesLaudosFacade()
				.buscaLaudoOrigemPaciente(solicitacaoExames.getSeq());
		if (dominioOrigemAtendimento != null
				&& !(dominioOrigemAtendimento.equals(DominioOrigemAtendimento.A) || dominioOrigemAtendimento
						.equals(DominioOrigemAtendimento.X))) {
			return null;
		}

//		if (unfExec != null
//				&& getRelatorioTicketExamesPacienteON().validarImpressaoExamePelaUnidade(itemSolicitacao, unfExec, materialAnalise)) {
//			continue;
//		}

		vo.setItemSolicitacaoExameSeqP(itemSolicitacao.getId().getSeqp().toString());
		AghUnidadesFuncionais unidadeFuncionalColeta = getRelatorioTicketExamesPacienteON().buscarUnidadeFuncionalColeta();
		vo.setUfeUnfSeq(getUfeUnfSeq(unidadeFuncionalColeta, caractUnidFuncionais, itemSolicitacao));

		vo.setSolicitacaoExameSeq(solicitacaoExames.getSeq().toString());

		vo.setAtdSeq(getAtdSeq(atendimento, atendimentoDiversos));

		vo.setOrigem(dominioOrigemAtendimento.toString());
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		vo.setCriadoEm(sdf1.format(solicitacaoExames.getCriadoEm()));

		vo.setNomeResp(getNomeResp(servidorResponsavel));

		vo.setUnfSeq(getUnfSeq(unidadeFuncionalColeta, caractUnidFuncionais));

		vo.setInformacoesClinicas(getRelatorioTicketExamesPacienteON().buscarInformacoesClinicas(
				solicitacaoExames.getInformacoesClinicas(), vo.getUnfSeq()));
		vo.setProntuario(getRelatorioTicketExamesPacienteON().buscaProntuario(solicitacaoExames).toString());
		vo.setNome(getRelatorioTicketExamesPacienteON().buscarLaudoNomePaciente(solicitacaoExames));
		Date dtNasc = getRelatorioTicketExamesPacienteON().buscarLaudoDataNascimento(solicitacaoExames);
		sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		vo.setNascimento(sdf1.format(dtNasc));
		vo.setIdade(DateUtil.obterIdadeFormatadaAnoMesDia(dtNasc));
		vo.setDescricaoUsual(aelExames.getDescricaoUsual());
		vo.setNatureza(Integer.toString(examesMaterialAnalise.getNatureza().getCodigo()));
		vo.setSigla(examesMaterialAnalise.getId().getExaSigla());
		vo.setManSeq(examesMaterialAnalise.getId().getManSeq().toString());
		
		if(examesMaterialAnalise.getTempoJejumNpo() != null) {
			vo.setTempoJejum(examesMaterialAnalise.getTempoJejumNpo());
			
		}
		maiorTempoJejum = getMaiorTempoJejumNpo(examesMaterialAnalise, maiorTempoJejum, vo);
		vo.setDescricaoMaterial(materialAnalise.getDescricao());
		vo.setDescricaoZona(getDescricaoZona(unidadeFuncionalColeta, caractUnidFuncionais));
		vo.setDescricaoUnidadeSolicitante(solicitacaoExames.getUnidadeFuncional().getDescricao());
		// CONVENIO
		// aelc_busca_conv_plan(DECODE(soeVAS.atd_seq,NULL,NULL,soeVAS.csp_cnv_codigo),DECODE(soeVAS.atd_seq,NULL,NULL,soeVAS.csp_seq)),
		if (atendimento!= null){
			convenioSaudePlano = pacienteFacade.obterConvenioSaudePlanoAtendimento(atendimento.getSeq());
		} else if (solicitacaoExames.getCspSeq()!= null && solicitacaoExames.getCspCnvCodigo() != null) {
			convenioSaudePlano = faturamentoFacade.obterConvenioSaudePlano(solicitacaoExames.getCspCnvCodigo(), solicitacaoExames.getCspSeq().byteValue());
		}
		String trecho1 = getRelatorioTicketExamesPacienteON().buscarConvenioPlano(atendimento == null ? null : convenioSaudePlano);
		String trecho2 = " /  ";
		// substr(aelc_busca_conv_plan(soeVAS.csp_cnv_codigo,soeVAS.csp_seq),1,instr(aelc_busca_conv_plan(soeVAS.csp_cnv_codigo,soeVAS.csp_seq),'/')-1),
		String argTrecho3 = getRelatorioTicketExamesPacienteON().buscarConvenioPlano(convenioSaudePlano);
		String trecho3 = argTrecho3.substring(0, argTrecho3.indexOf('/') - 1);
		// aelc_busca_conv_plan(DECODE(soeVAS.atd_seq,NULL,NULL,soeVAS.csp_cnv_codigo)
		// ,DECODE(soeVAS.atd_seq,NULL,NULL,soeVAS.csp_seq))
		String trecho4 = trecho1;
		vo.setConvenio(trecho1 == trecho2 ? trecho3 : trecho4);
		vo.setCnvCodigo(getCnvCodigo(atendimento, convenioSaudePlano));
		vo.setCspSeq(getCspSeq(atendimento, convenioSaudePlano));

		// tempo_realizacao_dias
		/*
		 * decode(UNIDADE_MEDIDA_TEMPO_REALIZACA, 'H',
		 * TEMPO_REALIZACAO_EXAME/24, 'M', (TEMPO_REALIZACAO_EXAME/60)/24,
		 * 'D', TEMPO_REALIZACAO_EXAME)
		 */
		Integer tempo = 0;
		vo.setTempoRealizacaoDias(getTempo(executaExames, tempo));
		vo.setUnfSeqComparece(executaExames.getUnfSeqComparece().getSeq().toString());
		// aelc_get_proj_atend
		vo.setProjeto(getProjeto(atendimento, atendimentoDiversos));
		vo.setUnidSolic(solicitacaoExames.getUnidadeFuncional().getSeq().toString());

		String novasRecomendacoes = getRelatorioTicketExamesPacienteON().buscarNovasRecomendacoes();
		// Recomendacoes
		vo.setRecomendacoes(getRelatorioTicketExamesPacienteON().buscarRecomendacoesExames(examesMaterialAnalise.getId().getExaSigla(),
				examesMaterialAnalise.getId().getManSeq(), novasRecomendacoes));

		// Agendamento
		vo.setAgendamento(getRelatorioTicketExamesPacienteON().buscarAgendamentoItemExame(itemSolicitacao));
		// Unidade Comparece
		vo.setTextoComparecer(getTextoComparecer(itemSolicitacao, caractUnidFuncionais,convenioSaudePlano.getConvenioSaude()));

		// codigo de barra item
		vo.setCodigoBarraItem(getRelatorioTicketExamesPacienteON().buscarCodigoBarraItem(itemSolicitacao));

		vo.setExamesAgendados("");

		vo.setLocalizador(getLocalizador(solicitacaoExames));
		return vo;
	}

	private String getLocalizador(AelSolicitacaoExames solicitacaoExames) {
		if (solicitacaoExames.getLocalizador() != null) {
			return solicitacaoExames.getLocalizador();
		}
		return null;
	}

	private String getUfeUnfSeq(AghUnidadesFuncionais unidadeFuncionalColeta, AghCaractUnidFuncionais caractUnidFuncionais,
			AelItemSolicitacaoExames itemSolicitacao) {
		if (caractUnidFuncionais != null && caractUnidFuncionais.getUnidadeFuncional() != null) {
			return itemSolicitacao.getUnidadeFuncional().getSeq().toString();
		} else {
			return unidadeFuncionalColeta.getSeq().toString();
		}
	}

	private String getUnfSeq(AghUnidadesFuncionais unidadeFuncionalColeta, AghCaractUnidFuncionais caractUnidFuncionais) {
		if (caractUnidFuncionais != null && caractUnidFuncionais.getUnidadeFuncional() != null) {
			return caractUnidFuncionais.getUnidadeFuncional().getSeq().toString();
		} else {
			return unidadeFuncionalColeta.getSeq().toString();
		}
	}

	private String getNomeResp(RapServidores servidorResponsavel) {
		String result = "";
		if (servidorResponsavel != null) {
			result = getRelatorioTicketExamesPacienteON().buscarNomeServ(servidorResponsavel.getId().getMatricula(),
					servidorResponsavel.getId().getVinCodigo());
		}
		return result;
	}

	private String getAtdSeq(AghAtendimentos atendimento, AelAtendimentoDiversos atendimentoDiversos) {
		if (atendimento != null) {
			return atendimento.getSeq().toString();
		} else {
			return atendimentoDiversos.getSeq().toString();
		}
	}

	private String getDescricaoZona(AghUnidadesFuncionais unidadeFuncionalColeta, AghCaractUnidFuncionais caractUnidFuncionais) {
		if (caractUnidFuncionais != null && caractUnidFuncionais.getUnidadeFuncional() != null) {
			return caractUnidFuncionais.getUnidadeFuncional().getDescricao();
		} else {
			return unidadeFuncionalColeta.getDescricao();
		}
	}

	private String getProjeto(AghAtendimentos atendimento, AelAtendimentoDiversos atendimentoDiversos) {
		if (atendimento != null || atendimentoDiversos != null) {
			return getRelatorioTicketExamesPacienteON().aelcGetProjAtend(atendimento, atendimentoDiversos);
		}
		return "";
	}

	private String getCspSeq(AghAtendimentos atendimento, FatConvenioSaudePlano convenioSaudePlano) {
		return atendimento == null ? null : convenioSaudePlano.getId().getSeq().toString();
	}

	private String getCnvCodigo(AghAtendimentos atendimento, FatConvenioSaudePlano convenioSaudePlano) {
		return atendimento == null ? null : convenioSaudePlano.getId().getCnvCodigo().toString();
	}

	private Integer getTempo(AelUnfExecutaExames executaExames, Integer tempo) {

        Double tempoRealizaExame = Double.valueOf(executaExames.getTempoRealizacaoExame() != null ? executaExames.getTempoRealizacaoExame() : 0);

        if (executaExames.getUnidadeMedidaTempoRealizaca().equals(DominioUnidTempoComMinuto.H)) {
			return arredondaTempoRealizacaoExame(tempoRealizaExame / 24);
		} else if (executaExames.getUnidadeMedidaTempoRealizaca().equals(DominioUnidTempoComMinuto.M)) {
			return arredondaTempoRealizacaoExame((tempoRealizaExame / 60) / 24);
		} else if (executaExames.getUnidadeMedidaTempoRealizaca().equals(DominioUnidTempoComMinuto.D)) {
			return executaExames.getTempoRealizacaoExame().intValue();
		}
		return tempo;
	}

    private Integer arredondaTempoRealizacaoExame(Double tempoRealizaExame) {
        if(tempoRealizaExame % 1 >= 0.1) {
            return tempoRealizaExame.intValue() + 1;
        }
        return tempoRealizaExame.intValue();
    }

	private String getTextoComparecer(AelItemSolicitacaoExames itemSolicitacao, AghCaractUnidFuncionais caractUnidFuncionais, FatConvenioSaude fatConvenioSaude)
			throws ApplicationBusinessException {
		if (fatConvenioSaude.getDescricao().equals(DominioGrupoConvenio.S.getDescricao())) {
			return getRelatorioTicketExamesPacienteON().buscarTextoComparecer(itemSolicitacao, caractUnidFuncionais);
		} else {
			return "";
		}
	}
	
	private Short getMaiorTempoJejumNpo(AelExamesMaterialAnalise examesMaterialAnalise, Short maiorTempoJejum, TicketExamesPacienteVO vo) {
		StringBuilder result = new StringBuilder(50);
		
		if ( examesMaterialAnalise.getTempoJejumNpo() != null ) {
			if( maiorTempoJejum == null ){
				result.append("Jejum: " + examesMaterialAnalise.getTempoJejumNpo().toString() + " "
					+ (examesMaterialAnalise.getTempoJejumNpo() > 1 ? "horas" : "hora"));
				maiorTempoJejum=examesMaterialAnalise.getTempoJejumNpo();
				//return result.toString();
			} else if(examesMaterialAnalise.getTempoJejumNpo() > maiorTempoJejum) {
				result.append("Jejum: " + examesMaterialAnalise.getTempoJejumNpo().toString() + " "
						+ (examesMaterialAnalise.getTempoJejumNpo() > 1 ? "horas" : "hora"));
					maiorTempoJejum=examesMaterialAnalise.getTempoJejumNpo();
					//return result.toString();
			}
		} else if ( examesMaterialAnalise.getTempoJejumNpo() == null && maiorTempoJejum == null) {
			result.append("Não necessita Jejum");
			//return result.toString();
		} 
		if ( examesMaterialAnalise.getTempoJejumNpo() == null && maiorTempoJejum != null) {
			result.append("Jejum: " + maiorTempoJejum.toString() + " "
					+ (maiorTempoJejum > 1 ? "horas" : "hora"));
				//return result.toString();
		} 

		vo.setTempoJejumNpo(result.toString());
		return maiorTempoJejum;
	}

	public AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public RelatorioTicketExamesPacienteON getRelatorioTicketExamesPacienteON() {
		return relatorioTicketExamesPacienteON;
	}

	public void setRelatorioTicketExamesPacienteON(
			RelatorioTicketExamesPacienteON relatorioTicketExamesPacienteON) {
		this.relatorioTicketExamesPacienteON = relatorioTicketExamesPacienteON;
	}
	
	public void pesquisarUnidadeFuncionalColeta()throws ApplicationBusinessException{
		AghUnidadesFuncionais unidadeFuncionalColeta = getRelatorioTicketExamesPacienteON().buscarUnidadeFuncionalColeta();
		if( unidadeFuncionalColeta == null){
			throw new ApplicationBusinessException(TickeExamenstPacienteONExceptionCode.P_COD_UNIDADE_COLETA_DEFAULT_NÃO_É_UMA_UND_FUNCIONAL_VÁLIDA);
		}
	}
}
