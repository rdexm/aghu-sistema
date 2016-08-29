package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.dominio.DominioTipoItemPrescricaoSumario;
import br.gov.mec.aghu.dominio.DominioTipoPrescricaoQuimioterapia;
import br.gov.mec.aghu.model.MptCidTratTerapeutico;
import br.gov.mec.aghu.model.MptControleFreqSessao;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.RelSumarioSessoesQuimioVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioQuimioItensPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioQuimioPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioSessoesQuimioVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("ucd")
@Stateless
public class SessoesTerapeuticasPOLON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(SessoesTerapeuticasPOLON.class);
	private static final long serialVersionUID = -866851985315392736L;
	

	@EJB
	private SessoesTerapeuticasPOLRN sessoesTerapeuticasPOLRN;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public enum SessoesTerapeuticasPOLONExceptionCode implements BusinessExceptionCode {
		AIP_00275
	}

	public List<NodoPOLVO> carregaNosFisioterapia(Integer codPaciente, String tipo, String pagina, String icone) throws ApplicationBusinessException {
		Integer tptSeq = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_TRAT_FISIOTERAPIA).getVlrNumerico().intValue();
		List<MptTratamentoTerapeutico> tratamentoTerapeuticos = getProcedimentoTerapeuticoFacade().listarSessoesFisioterapia(codPaciente, tptSeq);
		List<NodoPOLVO> list = new ArrayList<>();
		
		for (MptTratamentoTerapeutico tratamentoTerapeutico : tratamentoTerapeuticos) {
			Integer seq = tratamentoTerapeutico.getSeq();
			String descricao = DateUtil.dataToString(tratamentoTerapeutico.getDthrInicio(),  DateConstants.DATE_PATTERN_DDMMYYYY);
			if(tratamentoTerapeutico.getDthrFim() != null){
				descricao = descricao.concat(" - ").concat(DateUtil.dataToString(tratamentoTerapeutico.getDthrFim(), DateConstants.DATE_PATTERN_DDMMYYYY));
			}
			NodoPOLVO folha = new NodoPOLVO(codPaciente,tipo,descricao,icone,pagina);
			folha.addParam("seqTratamentoTerapeutico", seq);
			list.add(folha);
		}
		return list;
	}
	
	
	
	public List<NodoPOLVO> carregaNosQuimioterapia(Integer codPaciente, String tipo, String pagina, String icone) throws ApplicationBusinessException {
		Integer tptSeq = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_TRAT_QUIMIO).getVlrNumerico().intValue();
		List<NodoPOLVO> list = new ArrayList<>();
		List<MptTratamentoTerapeutico> tratamentoTerapeuticos = getProcedimentoTerapeuticoFacade().listarTratamentosTerapeuticosPorPacienteTptSeq(codPaciente, tptSeq);
		for (MptTratamentoTerapeutico tratamentoTerapeutico : tratamentoTerapeuticos) {
			Integer seq = tratamentoTerapeutico.getSeq();
			String descricao = DateUtil.dataToString(tratamentoTerapeutico.getDthrInicio(), DateConstants.DATE_PATTERN_DDMMYYYY);
			if(tratamentoTerapeutico.getDthrFim() != null){
				descricao = descricao.concat(" - ").concat(DateUtil.dataToString(tratamentoTerapeutico.getDthrFim(), DateConstants.DATE_PATTERN_DDMMYYYY));
			}
			NodoPOLVO folha = new NodoPOLVO(codPaciente,tipo,descricao,icone,pagina);
			folha.addParam("seqTratamentoTerapeutico", seq);
			list.add(folha);
		}
		return list;
	}
	
	/**
	 * Método que carrega a lista quinzenal de seleção para o relatório
	 * de sessões de quimio
	 * #18505
	 * 
	 * @throws ApplicationBusinessException 
	 */
	public List<SumarioSessoesQuimioVO> montarQuinzenaSessoesQuimio(Integer trpSeq) throws ApplicationBusinessException {

		Integer atdSeq = obterAtdSeq(trpSeq);
		//C1
		List<MptPrescricaoPaciente> prescricoesPaciente = getProcedimentoTerapeuticoFacade().listarPeriodosSessoesQuimio(atdSeq);
		if(prescricoesPaciente == null || prescricoesPaciente.size() < 1){
			throw new ApplicationBusinessException(SessoesTerapeuticasPOLONExceptionCode.AIP_00275);
		}
		List<MptPrescricaoPaciente> aux = new ArrayList<MptPrescricaoPaciente>(); 
		List<SumarioSessoesQuimioVO> quinzenasQuimio = new ArrayList<SumarioSessoesQuimioVO>();
		
		Integer idx = 0;

		if(prescricoesPaciente != null && prescricoesPaciente.size() > 0){
			while(prescricoesPaciente.subList(idx*15, prescricoesPaciente.size()).size() > 0) {
				if(prescricoesPaciente.subList(idx*15, prescricoesPaciente.size()).size() > 15){
					aux = prescricoesPaciente.subList(idx*15, (1+idx)*15);

					SumarioSessoesQuimioVO vo = new SumarioSessoesQuimioVO();
					vo.setDtInicio(aux.get(0).getDataPrevisaoExecucao());
					vo.setDtFim(aux.get(14).getDataPrevisaoExecucao());
					vo.setIdx(idx);
					vo.setAtdSeq(atdSeq);
					vo.setColecao(preencherRelSumarioSessoesQuimioVO(atdSeq, vo.getDtInicio(), vo.getDtFim()));
					quinzenasQuimio.add(vo);
					idx++;
				}else{
					aux = prescricoesPaciente.subList(idx*15, prescricoesPaciente.size());
					
					SumarioSessoesQuimioVO vo = new SumarioSessoesQuimioVO();
					vo.setDtInicio(aux.get(0).getDataPrevisaoExecucao());
					vo.setDtFim(aux.get(aux.size() -1).getDataPrevisaoExecucao());
					vo.setIdx(idx);
					vo.setAtdSeq(atdSeq);
					vo.setColecao(preencherRelSumarioSessoesQuimioVO(atdSeq, vo.getDtInicio(), vo.getDtFim()));
					quinzenasQuimio.add(vo);
					break;
				}
			}
		}
		
		Collections.reverse(quinzenasQuimio);
		return quinzenasQuimio;
	}
	
	private DominioTipoEmissaoSumario getParametroTipoEmissao() {
		DominioTipoEmissaoSumario parametroTipoEmissao = DominioTipoEmissaoSumario.P;
		return parametroTipoEmissao;
	}

	public List<RelSumarioSessoesQuimioVO> preencherRelSumarioSessoesQuimioVO(Integer atdSeq, Date dtInicio, Date dtFim) throws ApplicationBusinessException{
		//Parametro fixo #18505
		DominioTipoEmissaoSumario parametroTipoEmissao = getParametroTipoEmissao();
		
		List<RelSumarioSessoesQuimioVO> listaRelatorio = new ArrayList<RelSumarioSessoesQuimioVO>();
		//Recupera o parametro apaSeq
		Integer apaSeq = getPacienteFacade().gerarAtendimentoPaciente(atdSeq);
		
		//C2
		SumarioQuimioPOLVO c2 = processarDadosCabecalhoC2(atdSeq, apaSeq);
		
		//C3
		List<SumarioQuimioItensPOLVO> c3 =  processarDadosPeriodosC3(dtInicio, dtFim, atdSeq, apaSeq);
		//C4
		List<String> c4 = processarDadosTituloC4(atdSeq, dtInicio, dtFim);
		
		StringBuffer protocolos = new StringBuffer();
		for (String protocolo : c4) {
			if(protocolos.length() == 0){
				protocolos.append(protocolo);
			}else{
				protocolos.append('\n').append(protocolo);
			}
		}
		
		// Preenche a lista com os dias
		List<Date> listaDias = new ArrayList<Date>();
		for(SumarioQuimioItensPOLVO consulta : c3){
			if(!listaDias.contains(consulta.getDiqData())){
				listaDias.add(consulta.getDiqData());
			}
		}
		
		CoreUtil.ordenarLista(listaDias, "time" , Boolean.TRUE);
		
		for(int i = 0; i< c3.size(); i++){
			
			RelSumarioSessoesQuimioVO dadosRelatorio = new RelSumarioSessoesQuimioVO();
			dadosRelatorio.setDataInicial(dtInicio);
			dadosRelatorio.setDataFim(dtFim);
			dadosRelatorio.setNomePaciente(c2.getPacNome());
			dadosRelatorio.setProntuario(c2.getPacProntuarioFormatado());
			dadosRelatorio.setNomeEquipe(c2.getEquipeEsp());
			dadosRelatorio.setDataInicioTratamento(c2.getAtdDthrInicioTrat());
			dadosRelatorio.setDataFimTratamento(c2.getAtdDthrFimTrat());
			dadosRelatorio.setTipoEmissao(parametroTipoEmissao.toString());
			dadosRelatorio.setUnidade(c2.getUnidade());
			dadosRelatorio.setDataAtual(new Date());
			
			if(c4.size() > 1){
				dadosRelatorio.setTextoProtocolo("Protocolos: ");
			}else{
				dadosRelatorio.setTextoProtocolo("Protocolo: ");
			}
			dadosRelatorio.setProtocolo(protocolos.toString());
			dadosRelatorio.setListaDias(listaDias);
			
			
			dadosRelatorio.setTipo(c3.get(i).getItqTipo().getDescricao().toUpperCase());
			dadosRelatorio.setDescricao(c3.get(i).getItqDescricao());
			
			//Itera pela lista de dias para preencher com o valor
			List<String> diasDescricao = new ArrayList<String>();
			for (int j = 0; j < dadosRelatorio.getListaDias().size(); j++) {
				diasDescricao.add("");
			}
			for(Date dia: dadosRelatorio.getListaDias()){
				while(dia.equals(c3.get(i).getDiqData())){
					if(c3.get(i).getDiqValor().equals("1") || c3.get(i).getDiqValor().equals("2")){
						c3.get(i).setDiqValor("");
					}
					setStatusByDiqValor(c3, i, dadosRelatorio, diasDescricao,dia);
					if((i+1 < c3.size()) && c3.get(i).getItqDescricao().equals(c3.get(i+1).getItqDescricao())){
						i++;
					}else{
						break;
					}
				}
			}
			dadosRelatorio.setStatus(diasDescricao);
			listaRelatorio.add(dadosRelatorio);
		}
		return listaRelatorio;
	}

	private void setStatusByDiqValor(List<SumarioQuimioItensPOLVO> c3, int i,
			RelSumarioSessoesQuimioVO dadosRelatorio,
			List<String> diasDescricao, Date dia) {
		if(!"".equals(diasDescricao.get(dadosRelatorio.getListaDias().indexOf(dia))) &&
				!diasDescricao.get(dadosRelatorio.getListaDias().indexOf(dia)).equals(c3.get(i).getDiqValor())){
			String newDiq = diasDescricao.get(dadosRelatorio.getListaDias().indexOf(dia)) + "\n" + c3.get(i).getDiqValor();
			diasDescricao.set(dadosRelatorio.getListaDias().indexOf(dia), newDiq);
		}else{
			diasDescricao.set(dadosRelatorio.getListaDias().indexOf(dia), c3.get(i).getDiqValor());
		}
	}
	
	
	public SumarioQuimioPOLVO processarDadosCabecalhoC2(Integer atdSeq, Integer apaSeq) throws ApplicationBusinessException{
		SumarioQuimioPOLVO vo = getAghuFacade().listarDadosCabecalhoSumarioPrescricaoQuimio(atdSeq, apaSeq);
		
		vo.setPacNome(getAmbulatorioFacade().mpmcMinusculo(vo.getPacNome(), 2));
		vo.setPacProntuarioFormatado(CoreUtil.formataProntuario(vo.getPacProntuario()));
		vo.setSessao(vo.getPacProntuario().toString().substring(vo.getPacProntuario().toString().length() -2, vo.getPacProntuario().toString().length()));
		vo.setDthrInternacao(getPrescricaoMedicaFacade().obterDataInternacao(vo.getAtdIntSeq(), vo.getAtdAtuSeq(), vo.getAtdHodSeq()));
		String nomeServidor = vo.getSerVinCodigo() != null ? getAmbulatorioFacade().
								mpmcMinusculo(
									getPrescricaoMedicaFacade().obtemNomeServidorEditado(
											vo.getSerVinCodigo(), vo.getSerMatricula()), 2) : ""; 
		String nomeEsp = vo.getEspNomeEspecialidade() != null ? getAmbulatorioFacade().mpmcMinusculo(vo.getEspNomeEspecialidade(), 1) : "";
		vo.setEquipeEsp(nomeServidor + " - " +  nomeEsp);
		
		vo.setLtoLtoId(getProntuarioOnlineFacade().
				obterLocalizacaoPacienteParaRelatorio(vo.getAtdLtoId(), vo.getAtdQrtNumero(), vo.getAtdUnfSeq()));
		vo.setUnidade(getSessoesTerapeuticasPOLRN().obterUnidadeAtendimentoPaciente(atdSeq));
		
		return vo;
	}
	
	public List<SumarioQuimioItensPOLVO> processarDadosPeriodosC3(Date dtInicio, Date dtFim, Integer apaAtdSeq, Integer apaSeq) throws ApplicationBusinessException{
		List<SumarioQuimioItensPOLVO> c3 = getProcedimentoTerapeuticoFacade().listarDadosPeriodosSumarioPrescricaoQuimio(dtInicio, dtFim, apaAtdSeq, apaSeq);
		
		for (SumarioQuimioItensPOLVO vo : c3) {
			vo.setItqTipo(DominioTipoPrescricaoQuimioterapia.getTipoPrescricaoQuimioterapia(vo.getItqTipoInteger()));
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(vo.getDiqData());
			vo.setDiqDataN((short) calendar.get(Calendar.DAY_OF_MONTH));
			vo.setDescrTipo(vo.getItqTipo().getDescricao());
			if(DominioTipoItemPrescricaoSumario.POSITIVO_6.equals(vo.getItqTipo())){
				vo.setOrdem(getSessoesTerapeuticasPOLRN().obterOrdemSumario(vo.getItqSeq()) + vo.getItqDescricao());
			}else{
				vo.setOrdem("999" + vo.getItqDescricao());
			}
			if(vo.getDiqValor().equalsIgnoreCase("P")){
				vo.setValorOrderBy("1");
			}if (vo.getDiqValor().equalsIgnoreCase("S")) {
				vo.setValorOrderBy("2");
			} else {
				vo.setValorOrderBy("3");
			}
			
			vo.setDiqDataFormatada(DateUtil.dataToString(vo.getDiqData(), "yyyyMMdd"));
		}
		
		HashSet<SumarioQuimioItensPOLVO> hash = new HashSet<SumarioQuimioItensPOLVO>(c3);
		c3 = new ArrayList<SumarioQuimioItensPOLVO>(hash);
		
		CoreUtil.ordenarLista(c3, SumarioQuimioItensPOLVO.Fields.VALOR_ORDER_BY.toString() , Boolean.FALSE);
		CoreUtil.ordenarLista(c3, SumarioQuimioItensPOLVO.Fields.DIQ_DATA_FORMATADA.toString() , Boolean.TRUE);
		CoreUtil.ordenarLista(c3, SumarioQuimioItensPOLVO.Fields.ORDEM.toString() , Boolean.TRUE);
		CoreUtil.ordenarLista(c3, SumarioQuimioItensPOLVO.Fields.ITQ_TIPO_INTEGER.toString() , Boolean.TRUE);
		
		
		return c3;
	}
	
	public List<String> processarDadosTituloC4(Integer atdSeq, Date dtInicio, Date dtFim) throws ApplicationBusinessException{
		List<String> c4 = getProcedimentoTerapeuticoFacade().listarTituloProtocoloAssistencial(atdSeq, dtInicio, dtFim);
		return c4;
	}
	
	/**
	 * Método que carrega o atdSeq
	 * #18505
	 * C5
	 */
	public Integer obterAtdSeq(Integer trpSeq) {
		Integer atdSeq = getProcedimentoTerapeuticoFacade().obterPrescricaoPacienteporTrpSeq(trpSeq);
		return atdSeq;
	}
	
	public List<MptControleFreqSessao> listarSessoesFisioterapia(Integer trpSeq, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getProcedimentoTerapeuticoFacade().listarSessoesFisioterapia(trpSeq, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long listarSessoesFisioterapiaCount(Integer trpSeq) {
		return getProcedimentoTerapeuticoFacade().listarSessoesFisioterapiaCount(trpSeq);
	}

	public MptCidTratTerapeutico obterCidTratamentoQuimio(
			Integer seqTratamentoTerQuimio) {
		List<MptCidTratTerapeutico> listaCid = getProcedimentoTerapeuticoFacade().listarCidTratamentoQuimio(seqTratamentoTerQuimio);
		if (listaCid != null && listaCid.size() > 0){
			return listaCid.get(0);
		} else {
			return null;
		}
	}
	
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return procedimentoTerapeuticoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	protected IProntuarioOnlineFacade getProntuarioOnlineFacade(){
		return prontuarioOnlineFacade;
	}
	
	protected SessoesTerapeuticasPOLRN getSessoesTerapeuticasPOLRN(){
		return sessoesTerapeuticasPOLRN;
	}	
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}	
}
