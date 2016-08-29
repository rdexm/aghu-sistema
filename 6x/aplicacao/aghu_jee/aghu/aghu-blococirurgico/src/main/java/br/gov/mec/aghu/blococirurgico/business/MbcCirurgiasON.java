package br.gov.mec.aghu.blococirurgico.business;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDestinoPacienteDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.blococirurgico.vo.PesquisarPacientesCirurgiaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDestinoPaciente;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio da tela de cadastro de cirurgia(MbcCirurgias).
 * 
 * @autor fwinck
 * 
 */
@Stateless
public class MbcCirurgiasON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcCirurgiasON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;
	
	@Inject
	private MbcDestinoPacienteDAO mbcDestinoPacienteDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;


	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	
	private static final long serialVersionUID = -9001235819703398140L;
	
	

	protected enum MbcCirurgiasONExceptionCode implements BusinessExceptionCode {
		MBC_00519;
	}

	public AipPacientes obterPacientePorLeito(final AinLeitos leito) throws BaseException {
		List<AghAtendimentos> listaAtendimentos = getAghuFacade().pesquisarAtendimentoVigentePrescricaoEnfermagemPorLeito(leito);
		
		if (!listaAtendimentos.isEmpty() && listaAtendimentos.get(0)!=null && listaAtendimentos.get(0).getPaciente()!=null){
			return listaAtendimentos.get(0).getPaciente();			
		}else{
			throw new ApplicationBusinessException(MbcCirurgiasONExceptionCode.MBC_00519);
		}
	}
	
	protected IAghuFacade getAghuFacade() { 
		return  iAghuFacade;
	}

	/**
	 * ORADB V_MBC_AGENDA_CIRGS
	 * @param paciente
	 * @return
	 */
	public List<PesquisarPacientesCirurgiaVO> pesquisarPacientesCirurgia(
			AipPacientes paciente) {
		List<PesquisarPacientesCirurgiaVO> list = new ArrayList<PesquisarPacientesCirurgiaVO>();
		//UNION 1
		List<MbcAgendas> agendas = getMbcAgendasDAO().getPesquisarPacientesCirurgiaUnion1(paciente.getCodigo());
		//UNION 2
		List<MbcProfCirurgias> cirurgias = getMbcAgendasDAO().getPesquisarPacientesCirurgiaUnion2(paciente.getCodigo());
		
		for(MbcAgendas agenda : agendas){
			PesquisarPacientesCirurgiaVO vo = setarVoAgendas(agenda);
			list.add(vo);
		}
		
		for(MbcProfCirurgias profCirurgias : cirurgias){
			PesquisarPacientesCirurgiaVO vo = setarVoCirurgia(profCirurgias);
			if(!list.contains(vo)){
				list.add(vo);
			}
		}
		
		return list;
	}

	private PesquisarPacientesCirurgiaVO setarVoCirurgia(
			MbcProfCirurgias profCirurgias) {
		PesquisarPacientesCirurgiaVO vo = new PesquisarPacientesCirurgiaVO();
		if(profCirurgias.getCirurgia().getAgenda() != null){
			vo.setAgdSeq(profCirurgias.getCirurgia().getAgenda().getSeq());
		}
		vo.setCrgSeq(profCirurgias.getCirurgia().getSeq());
		if(profCirurgias.getCirurgia().getSituacao().equals(DominioSituacaoCirurgia.CANC)){
			vo.setCodigoContexto(String.valueOf(DominioSituacaoAgendas.CA.getCodigo()));
			vo.setDescricaoContexto(DominioSituacaoAgendas.CA.getDescricao());
		}else if(profCirurgias.getCirurgia().getSituacao().equals(DominioSituacaoCirurgia.RZDA)){
			vo.setCodigoContexto(String.valueOf(DominioSituacaoCirurgia.RZDA.getCodigo()));
			vo.setDescricaoContexto(getResourceBundleValue("LABEL_CIRURGIA_REALIZADA")); 
		}else{ 
			vo.setCodigoContexto(String.valueOf(DominioSituacaoAgendas.ES.getCodigo()));
			vo.setDescricaoContexto(DominioSituacaoAgendas.ES.getDescricao());
		}		
		vo.setMotivoCancContexto(profCirurgias.getCirurgia().getComplementoCanc());
		vo.setSiglaLocal(profCirurgias.getCirurgia().getUnidadeFuncional().getSigla());
		vo.setDescricaoLocal(profCirurgias.getCirurgia().getUnidadeFuncional().getDescricao());
		vo.setSiglaEspecialidade(profCirurgias.getCirurgia().getEspecialidade().getSigla());
		vo.setNomeEspecialidade(profCirurgias.getCirurgia().getEspecialidade().getNomeEspecialidade());
		if(profCirurgias.getCirurgia().getAgenda() != null){
			vo.setIndExclusao(profCirurgias.getCirurgia().getAgenda().getIndExclusao());
		}
		if(profCirurgias.getServidorPuc().getPessoaFisica().getNomeUsual() != null){
			vo.setEquipe(profCirurgias.getServidorPuc().getPessoaFisica().getNomeUsual());
		}else{
			vo.setEquipe(profCirurgias.getServidorPuc().getPessoaFisica().getNome());
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		vo.setDtAgenda(sdf.format(profCirurgias.getCirurgia().getData()));
		
		List<MbcProcEspPorCirurgias> espPorCirurgias = getMbcProcEspPorCirurgiasDao().pesquisarProcEspPorCirurgiasAtivaPrincipalPorCrgSeq(profCirurgias.getCirurgia().getSeq());
		if(espPorCirurgias != null && !espPorCirurgias.isEmpty()){
			vo.setProcedimentoPrincipal(getDescricaoProcedimentoPrincipal(espPorCirurgias));
		}
		return vo;
	}

	/**
	 * @ORADB MBCC_PROC_PRIN_SEQ
	 * @param espPorCirurgias
	 * @return
	 */
	private String getDescricaoProcedimentoPrincipal(List<MbcProcEspPorCirurgias> espPorCirurgias) {
		String descricaoProcPrinc = "";
		if(espPorCirurgias != null && !espPorCirurgias.isEmpty()){
			//Ordena por ind_resp_proc e pega o primeiro da lista
			Collections.sort(espPorCirurgias, new Comparator<MbcProcEspPorCirurgias>() {
				@Override
				public int compare(MbcProcEspPorCirurgias o1, MbcProcEspPorCirurgias o2) {
					return o1.getId().getIndRespProc().getCodigoCirurgiasCanceladas().compareTo(o2.getId().getIndRespProc().getCodigoCirurgiasCanceladas());
				}
			});
			
			descricaoProcPrinc = espPorCirurgias.get(0).getProcedimentoCirurgico().getDescricao();
		}
		return descricaoProcPrinc;
	}

	private PesquisarPacientesCirurgiaVO setarVoAgendas(MbcAgendas agenda) {
		PesquisarPacientesCirurgiaVO vo = new PesquisarPacientesCirurgiaVO();
		vo.setAgdSeq(agenda.getSeq());
		vo.setCodigoContexto(String.valueOf(agenda.getIndSituacao().getCodigo()));
		vo.setDescricaoContexto(agenda.getIndSituacao().getDescricao());
		vo.setSiglaLocal(agenda.getUnidadeFuncional().getSigla());
		vo.setDescricaoLocal(agenda.getUnidadeFuncional().getDescricao());
		vo.setSiglaEspecialidade(agenda.getEspecialidade().getSigla());
		vo.setNomeEspecialidade(agenda.getEspecialidade().getNomeEspecialidade());
		vo.setIndExclusao(agenda.getIndExclusao());
		if(agenda.getPucServidor().getPessoaFisica().getNomeUsual() != null){
			vo.setEquipe(agenda.getPucServidor().getPessoaFisica().getNomeUsual());
		}else{
			vo.setEquipe(agenda.getPucServidor().getPessoaFisica().getNome());
		}
		if(agenda.getDtAgenda()!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			vo.setDtAgenda(sdf.format(agenda.getDtAgenda()));
		}
		vo.setProcedimentoPrincipal(agenda.getProcedimentoCirurgico().getDescricao());
		return vo;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO(){
		return mbcAgendasDAO;
	}
	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDao() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO(){
		return mbcCirurgiasDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
		
	
	public MbcCirurgias prepararCirurgia(CirurgiaTelaVO vo, final DominioSituacaoCirurgia situacaoCirurgia) {
		MbcCirurgias retorno = null;
		
		MbcCirurgias original = this.getMbcCirurgiasDAO().obterPorChavePrimaria(vo.getCirurgia().getSeq());
		
		if (original != null) { // ATUALIZAR
			retorno = original;
			Integer version = original.getVersion();
			
			copiarPropriedades(retorno, vo);
			
			retorno.setVersion(version);
			
			if (vo.getCirurgia().getEspecialidade() != null) {
				AghEspecialidades especialidade = getAghuFacade().obterEspecialidade(vo.getCirurgia().getEspecialidade().getSeq());
				retorno.setEspecialidade(especialidade);
			}
			
			if (vo.getCirurgia().getDestinoPaciente() != null) {
				MbcDestinoPaciente destinoPaciente = mbcDestinoPacienteDAO.obterPorChavePrimaria(vo.getCirurgia().getDestinoPaciente().getSeq());
				retorno.setDestinoPaciente(destinoPaciente);
			}
			
			if (vo.getCirurgia().getAtendimento() != null) {
				AghAtendimentos atd = getAghuFacade().buscarAtendimentoPorSeq(vo.getCirurgia().getAtendimento().getSeq());
				retorno.setAtendimento(atd);				
			}
			
			if (vo.getCirurgia().getAgenda() != null) {
				MbcAgendas agenda = this.getMbcAgendasDAO().obterPorChavePrimaria(vo.getCirurgia().getAgenda().getSeq());
				retorno.setAgenda(agenda);
			}
			
			
			if (vo.getCirurgia().getConvenioSaude() != null) {
				FatConvenioSaude convSaude = faturamentoFacade.obterConvenioSaude(vo.getCirurgia().getConvenioSaude().getCodigo());
				retorno.setConvenioSaude(convSaude);
			}
			
			if (vo.getCirurgia().getConvenioSaudePlano() != null) {
				FatConvenioSaudePlano csp = faturamentoFacade.obterConvenioSaudePlano(
								vo.getCirurgia().getConvenioSaudePlano().getId().getCnvCodigo()
								, vo.getCirurgia().getConvenioSaudePlano().getId().getSeq()
				);
				retorno.setConvenioSaudePlano(csp);
			}
			
		}// FIM DO ATUALIZAR 
		else { // INSERIR
			retorno = new MbcCirurgias();
			
			copiarPropriedades(retorno, vo);
			
			if (vo.getCirurgia().getAtendimento() != null) {
				AghAtendimentos atd = getAghuFacade().buscarAtendimentoPorSeq(vo.getCirurgia().getAtendimento().getSeq());
				retorno.setAtendimento(atd);				
			}
			// Seta atributos com valores padrão
			retorno.setSeq(null);
			retorno.setSituacao(situacaoCirurgia);
			retorno.setContaminacao(false);
			retorno.setDigitaNotaSala(false);
			retorno.setPrecaucaoEspecial(false);
			retorno.setUtilizaO2(false);
			retorno.setUtilizaProAzot(false);
			retorno.setTemDescricao(false);
			retorno.setOverbooking(false);
			retorno.setAplicaListaCirurgiaSegura(false);
			retorno.setIndPrc(false);
			Calendar dataMomento = Calendar.getInstance();
			dataMomento.setTime(vo.getCirurgia().getData());
			if (retorno.getDataPrevisaoInicio() != null) {
				Calendar dataInicio = Calendar.getInstance();
				dataInicio.setTime(retorno.getDataPrevisaoInicio());
				dataInicio.set(Calendar.DAY_OF_MONTH, dataMomento.get(Calendar.DAY_OF_MONTH));
				dataInicio.set(Calendar.MONTH, dataMomento.get(Calendar.MONTH));
				dataInicio.set(Calendar.YEAR, dataMomento.get(Calendar.YEAR));
				retorno.setDataPrevisaoInicio(dataInicio.getTime());
			}
			if (retorno.getDataPrevisaoFim() != null) {
				Calendar dataFim = Calendar.getInstance();
				dataFim.setTime(retorno.getDataPrevisaoFim());
				dataFim.set(Calendar.DAY_OF_MONTH, dataMomento.get(Calendar.DAY_OF_MONTH));
				dataFim.set(Calendar.MONTH, dataMomento.get(Calendar.MONTH));
				dataFim.set(Calendar.YEAR, dataMomento.get(Calendar.YEAR));
				retorno.setDataPrevisaoFim(dataFim.getTime());
			}
		}// FIM INSERIR
		
		return retorno;
	}

	private void copiarPropriedades(MbcCirurgias retorno, CirurgiaTelaVO vo) {
		try {
			BeanUtils.copyProperties(retorno, vo.getCirurgia());
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOG.error(e.getMessage(), e);
		}
	}

}