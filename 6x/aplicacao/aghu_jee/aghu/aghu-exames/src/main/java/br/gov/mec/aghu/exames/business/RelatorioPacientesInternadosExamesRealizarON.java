package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelRecomendacaoExameDAO;
import br.gov.mec.aghu.exames.vo.RelatorioPacientesInternadosExamesRealizarDadosVO;
import br.gov.mec.aghu.exames.vo.RelatorioPacientesInternadosExamesRealizarVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelRecomendacaoExame;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;

@Stateless
public class RelatorioPacientesInternadosExamesRealizarON extends BaseBusiness {


@EJB
private RelatorioTicketExamesPacienteON relatorioTicketExamesPacienteON;

private static final Log LOG = LogFactory.getLog(RelatorioPacientesInternadosExamesRealizarON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelRecomendacaoExameDAO aelRecomendacaoExameDAO;

@Inject
private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;

@EJB
private IAmbulatorioFacade ambulatorioFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5848451877541286234L;
	public final static String FORMATO_DATA_DDMMYY = "dd/MM/yy";
	public final static String FORMATO_DATA_DDMMYYYY_HHMM = "dd/MM/yyyy HH:mm";

	public RelatorioPacientesInternadosExamesRealizarVO pesquisarRelatorioPacientesInternadosExamesRealizar(AghUnidadesFuncionais unidadeFuncional,	DominioSimNao imprimeRecomendacoesExame, Boolean jejumNpo,
			Boolean preparo, Boolean dietaDiferenciada,	Boolean unidadeEmergencia, Boolean todos) throws ApplicationBusinessException {
		
		RelatorioPacientesInternadosExamesRealizarVO vo = new RelatorioPacientesInternadosExamesRealizarVO();
		recuperarRelatorioPacientesInternadosJejumNpo(unidadeFuncional, imprimeRecomendacoesExame, jejumNpo, vo);
		recuperarRelatorioPacientesInternadosPreparo(unidadeFuncional, imprimeRecomendacoesExame, preparo, vo);
		recuperarRelatorioPacientesInternadosDietaDiferenciada(unidadeFuncional, imprimeRecomendacoesExame,	dietaDiferenciada, vo);
		recuperarRelatorioPacientesInternadosUnidadeEmergencia(unidadeFuncional, imprimeRecomendacoesExame,	unidadeEmergencia, vo);
		recuperarRelatorioPacientesInternadosTodos(unidadeFuncional, imprimeRecomendacoesExame, todos, vo);
		
		return vo;
		
	}

	private void recuperarRelatorioPacientesInternadosJejumNpo(AghUnidadesFuncionais unidadeFuncional,DominioSimNao imprimeRecomendacoesExame, Boolean jejumNpo, RelatorioPacientesInternadosExamesRealizarVO vo) throws ApplicationBusinessException {
		if(jejumNpo){
			List<RelatorioPacientesInternadosExamesRealizarDadosVO> dados = new ArrayList<RelatorioPacientesInternadosExamesRealizarDadosVO>();
			List<AelItemSolicitacaoExames> itens = getAelItemSolicitacaoExameDAO().buscarRelatorioPacientesInternadosJejumNpo(unidadeFuncional);
			
			for(AelItemSolicitacaoExames item : itens){

				RelatorioPacientesInternadosExamesRealizarDadosVO dado = new RelatorioPacientesInternadosExamesRealizarDadosVO();
				dado.setIndUnidInternacao("Relação de Pacientes Internados com exames a realizar (Jejum/NPO)");
				dado.setCriadoEm(DateUtil.obterDataFormatada(item.getDthrProgramada(),FORMATO_DATA_DDMMYY)); //soe.criado_em
				dado.setHedDthrAgenda(this.buscarHedDthrAgenda(item));
				dado.setDescricaoUsual(item.getExame().getDescricaoUsual()+"/"+item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getDescricao());
				setarVO(imprimeRecomendacoesExame, item,dado);
				if (dado.getSeqp() != null && dado.getSoeSeq() != null) {
					dados.add(dado);
				}
			}
			Collections.sort(dados);
			vo.setJejumNpo(dados);
		}
	}

	private void recuperarRelatorioPacientesInternadosTodos(AghUnidadesFuncionais unidadeFuncional,	DominioSimNao imprimeRecomendacoesExame, Boolean todos,	RelatorioPacientesInternadosExamesRealizarVO vo) throws ApplicationBusinessException {
		if(todos){
			List<RelatorioPacientesInternadosExamesRealizarDadosVO> dados = new ArrayList<RelatorioPacientesInternadosExamesRealizarDadosVO>();
			List<AelItemSolicitacaoExames> itens = getAelItemSolicitacaoExameDAO().buscarRelatorioPacientesInternadosTodos(unidadeFuncional);
						
			for(AelItemSolicitacaoExames item : itens){

				RelatorioPacientesInternadosExamesRealizarDadosVO dado = new RelatorioPacientesInternadosExamesRealizarDadosVO();
				dado.setIndUnidInternacao("Relação de Pacientes Internados com exames a realizar(todos)");
				dado.setCriadoEm(DateUtil.obterDataFormatada(item.getSolicitacaoExame().getCriadoEm(), FORMATO_DATA_DDMMYY)); 
				dado.setHedDthrAgenda(this.buscarHedDthrAgenda(item));
				dado.setDescricaoUsual(item.getExame().getDescricaoUsual()+" "+item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getDescricao());
				setarVO(imprimeRecomendacoesExame, item,dado);
				if (dado.getSeqp() != null && dado.getSoeSeq() != null) {
					dados.add(dado);
				}
			}
			Collections.sort(dados);
			vo.setTodos(dados);
		}
	}

	private void recuperarRelatorioPacientesInternadosUnidadeEmergencia(AghUnidadesFuncionais unidadeFuncional,	DominioSimNao imprimeRecomendacoesExame, Boolean unidadeEmergencia,	RelatorioPacientesInternadosExamesRealizarVO vo) throws ApplicationBusinessException {
		if(unidadeEmergencia){
			List<RelatorioPacientesInternadosExamesRealizarDadosVO> dados = new ArrayList<RelatorioPacientesInternadosExamesRealizarDadosVO>();
			List<AelItemSolicitacaoExames> itens = getAelItemSolicitacaoExameDAO().buscarRelatorioPacientesInternadosUnidadeEmergencia(unidadeFuncional);
			
			for(AelItemSolicitacaoExames item : itens){

				RelatorioPacientesInternadosExamesRealizarDadosVO dado = new RelatorioPacientesInternadosExamesRealizarDadosVO();
				dado.setIndUnidInternacao("Relação de Pacientes Internados com exames a realizar(Emergência)");
				dado.setCriadoEm(DateUtil.obterDataFormatada(item.getSolicitacaoExame().getCriadoEm(),FORMATO_DATA_DDMMYY)); 
				dado.setHedDthrAgenda(this.buscarHedDthrAgenda(item));
				dado.setDescricaoUsual(item.getExame().getDescricaoUsual()+" "+item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getDescricao());
				setarVO(imprimeRecomendacoesExame, item,dado);
				if (dado.getSeqp() != null && dado.getSoeSeq() != null) {
					dados.add(dado);
				}
			}
			Collections.sort(dados);
			vo.setUnidadeEmergencia(dados);
		}
	}

	private void recuperarRelatorioPacientesInternadosDietaDiferenciada(AghUnidadesFuncionais unidadeFuncional,	DominioSimNao imprimeRecomendacoesExame, Boolean dietaDiferenciada,	RelatorioPacientesInternadosExamesRealizarVO vo) throws ApplicationBusinessException {
		if(dietaDiferenciada){
			List<RelatorioPacientesInternadosExamesRealizarDadosVO> dados = new ArrayList<RelatorioPacientesInternadosExamesRealizarDadosVO>();
			List<AelItemSolicitacaoExames> itens = getAelItemSolicitacaoExameDAO().buscarRelatorioPacientesInternadosDietaDiferenciada(unidadeFuncional);
			
			for(AelItemSolicitacaoExames item : itens){

				RelatorioPacientesInternadosExamesRealizarDadosVO dado = new RelatorioPacientesInternadosExamesRealizarDadosVO();
				dado.setIndUnidInternacao("Relação de Pacientes Internados com exames a realizar (Dieta Diferenciada)");
				dado.setCriadoEm(DateUtil.obterDataFormatada(item.getSolicitacaoExame().getCriadoEm(), FORMATO_DATA_DDMMYY)); 
				dado.setHedDthrAgenda(this.buscarHedDthrAgenda(item));
				dado.setDescricaoUsual(item.getExame().getDescricaoUsual()+"/"+item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getDescricao());
				setarVO(imprimeRecomendacoesExame, item,dado);
				if (dado.getSeqp() != null && dado.getSoeSeq() != null) {
					dados.add(dado);
				}
			}
			Collections.sort(dados);
			vo.setDietaDiferenciada(dados);
		}
	}

	private void recuperarRelatorioPacientesInternadosPreparo(AghUnidadesFuncionais unidadeFuncional, DominioSimNao imprimeRecomendacoesExame, Boolean preparo,	RelatorioPacientesInternadosExamesRealizarVO vo) throws ApplicationBusinessException {
		if(preparo){
			List<RelatorioPacientesInternadosExamesRealizarDadosVO> dados = new ArrayList<RelatorioPacientesInternadosExamesRealizarDadosVO>();
			List<AelItemSolicitacaoExames> itens = getAelItemSolicitacaoExameDAO().buscarRelatorioPacientesInternadosPreparo(unidadeFuncional);
			
			for(AelItemSolicitacaoExames item : itens){

				RelatorioPacientesInternadosExamesRealizarDadosVO dado = new RelatorioPacientesInternadosExamesRealizarDadosVO();
				dado.setIndUnidInternacao("Relação de Pacientes Internados com exames a realizar(Preparo)");
				dado.setCriadoEm(DateUtil.obterDataFormatada(item.getSolicitacaoExame().getCriadoEm(), FORMATO_DATA_DDMMYY)); 
				dado.setHedDthrAgenda(this.buscarHedDthrAgenda(item));
				dado.setDescricaoUsual(item.getExame().getDescricaoUsual()+" "+item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getDescricao());
				setarVO(imprimeRecomendacoesExame, item,dado);
				if (dado.getSeqp() != null && dado.getSoeSeq() != null) {
					dados.add(dado);
				}
			}
			
			Collections.sort(dados);
			vo.setPreparo(dados);
		}
	}

	private void setarVO(DominioSimNao imprimeRecomendacoesExame, AelItemSolicitacaoExames item, RelatorioPacientesInternadosExamesRealizarDadosVO dado) throws ApplicationBusinessException {
		
		if(imprimeRecomendacoesExame.equals(DominioSimNao.S)) {
			dado.setRecomendacoes(buscarRecomendacoesExames(item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getId().getExaSigla(),item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getId().getManSeq()));
		}
		
		dado.setUnfDescricao(item.getSolicitacaoExame().getUnidadeFuncional().getSeq()+" - "+item.getSolicitacaoExame().getUnidadeFuncional().getDescricao());
		dado.setAndarAlaDescricao(item.getSolicitacaoExame().getUnidadeFuncional().getAndarAlaDescricao());
		dado.setAtdSeq(item.getSolicitacaoExame().getAtendimento().getSeq().toString());
		
		//String resumoLocalPaciente = getPrescricaoMedicaFacade().buscarResumoLocalPaciente(item.getSolicitacaoExame().getAtendimento());
		
		//Criar metodo que preencha uma lista de leitos 
		
		AghAtendimentos aghAtendimento = ambulatorioFacade.buscarAtendimentoPossuiMesmoLeitoUnidFuncional(item.getSolicitacaoExame().getAtendimento() , item.getSolicitacaoExame().getUnidadeFuncional());
		
		if (aghAtendimento != null) {
			String resumoLocalPaciente = getPrescricaoMedicaFacade().buscarResumoLocalPacienteUniFuncional(aghAtendimento);
			if("EMERGENCIA".equals(resumoLocalPaciente)){
				dado.setDescMaterialAnalise("EME");
			}else{
				dado.setDescMaterialAnalise(resumoLocalPaciente);
			}
		
			dado.setNroAmostras(getRelatorioTicketExamesPacienteON().buscarLaudoProntuarioPaciente(item.getSolicitacaoExame()));
			dado.setDescMaterialAnalise1(item.getSolicitacaoExame().getAtendimento().getPaciente().getNome());
			dado.setExaSigla(item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getId().getExaSigla());
			dado.setManSeq(item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getId().getManSeq().toString());
			
			
			dado.setSeq1(item.getSolicitacaoExame().getSeq().toString());
			dado.setDescMaterialAnalise7(item.getUnidadeFuncional().getDescricao());
			dado.setDescMaterialAnalise2(this.converterStringBooleanaSN(item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getIndJejum()));
			dado.setDescMaterialAnalise3(this.converterStringBooleanaSN(item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getIndNpo()));
			dado.setDescMaterialAnalise6(this.converterStringBooleanaSN(item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getIndPreparo()));
			dado.setDescMaterialAnalise5(this.converterStringBooleanaSN(item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getIndDietaDiferenciada()));
			
			if(item.getRegiaoAnatomica() != null){
				dado.setRanSeq(item.getRegiaoAnatomica().getDescricao());
			}else{
				dado.setRanSeq(item.getDescRegiaoAnatomica());
			}
			
			if(item.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getIndJejum()){
				dado.setDescMaterialAnalise8("Jejum a partir das               horas");
			}else{
				dado.setDescMaterialAnalise8("Observações:");
			}
			
			dado.setSoeSeq(item.getId().getSoeSeq().toString());
			dado.setSeqp(item.getId().getSeqp().toString());
		} else {
			dado = null;
		}
	}

	private String buscarRecomendacoesExames(String sigla, Integer manSeq) {
		List<AelRecomendacaoExame> lista = getAelRecomendacaoExameDAO()
				.obterRecomendacoesExameResponsavelP(sigla, manSeq);// novasRecomendacoes
		
		StringBuffer recomendacoes = new StringBuffer();
		for (AelRecomendacaoExame re : lista) {
			recomendacoes.append(re.getDescricao()).append('\n');
		}
		return recomendacoes.toString();
	}
	
	/**
	 * Converte um valor booleano para uma String no padrao "S/N": 
	 * Vide: "S" para true e "N" para false 
	 * @param valor
	 * @return
	 */
	private String converterStringBooleanaSN(final Boolean valor){
		if(valor != null){
			if(valor){
				return "S";
			} else{
				return "N";
			}
		}
		return null;
	}

	/**
	 * 
	 * @param itemSolicitacaoExame
	 * @return
	 */
	protected String buscarHedDthrAgenda(AelItemSolicitacaoExames itemSolicitacaoExame) {
		Date hedDthrAgenda = getAelItemHorarioAgendadoDAO().buscarMenorHedDthrAgenda(itemSolicitacaoExame.getId().getSoeSeq(),
				itemSolicitacaoExame.getId().getSeqp());

		if (hedDthrAgenda != null) {
			return DateUtil.obterDataFormatada(hedDthrAgenda, FORMATO_DATA_DDMMYYYY_HHMM);
		}
		return null;
	}

	protected AelRecomendacaoExameDAO getAelRecomendacaoExameDAO() {
		return aelRecomendacaoExameDAO;
	}
	
	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	public AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO(){
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}

	
	protected RelatorioTicketExamesPacienteON getRelatorioTicketExamesPacienteON() {
		return relatorioTicketExamesPacienteON;
	}
}
