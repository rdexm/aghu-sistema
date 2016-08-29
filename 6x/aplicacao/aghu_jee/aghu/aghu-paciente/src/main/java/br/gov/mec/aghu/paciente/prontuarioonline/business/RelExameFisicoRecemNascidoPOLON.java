package br.gov.mec.aghu.paciente.prontuarioonline.business;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioMomento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.McoAchado;
import br.gov.mec.aghu.model.McoAchadoExameFisicos;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoExameFisicoRns;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoNotaAdicional;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.McoSindrome;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.RelExameFisicoRecemNascidoVO;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelExameFisicoRecemNascidoPOLON extends BaseBusiness{
	
	private static final Log LOG = LogFactory.getLog(RelExameFisicoRecemNascidoPOLON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IPerinatologiaFacade perinatologiaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AipPacientesDAO pacienteDAO;

	
	private static final long serialVersionUID = 378104325381178812L;

	public List<RelExameFisicoRecemNascidoVO> preencheRelExameFisicoRecemNascidoVO(Integer pacCodigo, Short gsoSeqp, Byte seqp, Integer conNumero) throws ApplicationBusinessException{

		RapServidores servidor = null;
		Date criadoEm = null;
		//Obtém resultado da C1
		List<McoLogImpressoes> logImpressoes = getPerinatologiaFacade().pesquisarLogImpressoesEventoMcorExFisicoRn(pacCodigo, gsoSeqp);
		if(logImpressoes != null && !logImpressoes.isEmpty()){
			servidor = logImpressoes.get(0).getServidor();
			criadoEm = logImpressoes.get(0).getCriadoEm();
		}
		
		RelExameFisicoRecemNascidoVO vo = new RelExameFisicoRecemNascidoVO();
		
		processarQPac(pacCodigo, gsoSeqp, conNumero, vo);
		processarQRecemNascido(pacCodigo, gsoSeqp, seqp, vo);
		processarQPacRn(vo.getRnaPacCodigo(), vo, pacCodigo, gsoSeqp, seqp);
		processarQAltura(conNumero, vo);
		processarQPeso(conNumero, vo);
		processarMcocVerConv(pacCodigo, conNumero, vo);
		processarQPep(vo);
		processarQAtp(vo);
		processarQEfr(pacCodigo, gsoSeqp, seqp, vo);
		if (vo.getSindromeSeq() != null){
			processarQSdm(vo.getSindromeSeq(), vo);
		}
		vo.setSubReport1(processarQAef(pacCodigo, gsoSeqp, seqp, vo));
		processarQ3(pacCodigo, gsoSeqp, seqp, vo);
		processarConsProf(servidor, vo);
		
		
		//Valores fixos
		vo.setNotaCabecalho("Este documento é uma compilação dos registros eletrônicos de diversos profissionais que atenderam a paciente.");
		vo.setDataAtual(new Date());
		if(criadoEm != null){
			vo.setDtHrMovimento(DateUtil.dataToString(criadoEm, "dd/MM/yyyy, HH:mm").concat(" h."));
		}else{
			vo.setDtHrMovimento(DateUtil.dataToString(new Date(), "dd/MM/yyyy, HH:mm").concat(" h."));
		}
		
		return Arrays.asList(vo);
	}

	private void processarConsProf(RapServidores servidor, RelExameFisicoRecemNascidoVO vo) throws ApplicationBusinessException {
		if(servidor != null){
			BuscaConselhoProfissionalServidorVO consProf = new BuscaConselhoProfissionalServidorVO();
			consProf = getPrescricaoMedicaFacade().buscaConselhoProfissionalServidorVO(servidor.getId().getMatricula(), servidor.getId().getVinCodigo(), Boolean.FALSE);
			if (consProf.getNome() != null){
				StringBuffer retorno = new StringBuffer();
				retorno.append(consProf.getNome().concat("  "))
				.append(consProf.getSiglaConselho().concat(" "))
				.append(consProf.getNumeroRegistroConselho());
				vo.setConsProf(retorno.toString());
			}
		}
	}

	private void processarQ3(Integer pacCodigo, Short gsoSeqp, Byte seqp,
			RelExameFisicoRecemNascidoVO vo) throws ApplicationBusinessException {
		McoNotaAdicional q3 = getPerinatologiaFacade().pesquisarNotaAdicionalPorPacienteGestacaoSeqp(pacCodigo, gsoSeqp, seqp);
		if (q3 != null){
			vo.setNotaAdicional(q3.getNotaAdicional());
			vo.setCriadoEm(DateUtil.dataToString(q3.getCriadoEm(), "dd/MM/yyyy, HH:mm").concat(" h"));
			if(q3.getSerMatricula() != null && q3.getSerMatricula() != null){
			    vo.setNomeProf(formataNomeProf(q3.getSerMatricula(), q3.getSerVinCodigo()));
//              ConselhoProfissionalServidorVO cPServidorVO = getRegistroColaboradorFacade().buscaConselhoProfissional(q3.getSerMatricula(), q3.getSerVinCodigo());
//				if (cPServidorVO != null){
//					StringBuffer retorno = new StringBuffer();
//					retorno.append(cPServidorVO.getNome().concat("  "));
//					retorno.append(cPServidorVO.getSiglaConselho().concat(" "));
//					retorno.append(cPServidorVO.getNumeroRegistroConselho());
//					vo.setNomeProf(retorno.toString());
//				}
			}
		}
	}

	/**
	 * @ORADB - MCOC_BUSCA_CONS_PROF
	 * Esta função original utilizava tabelas CSE (cse_acoes, cse_acoes_con_profissionais, etc)
	 * Foi migrada para utilizar o CASCA, ou seja, na função original é feito:
	 * 		aco.descricao 		= 	'VALIDAR PRESCRICAO MEDICA'
	 * agora é feito atráves de:
	 * 		getCascaFacade().usuarioTemPermissao(servidor.getUsuario(), "confirmarPrescricaoMedica");
	 *  
	 * Importante lembrar que permissão que está sendo verificada é do usuário que está sendo passado como parametro.
	 * Então se é passado a matricula e vinCodigo de um Médico, é verificado se 
	 * este médido tem a permissão "confirmarPrescricaoMedica" (o médico tem que ter cadastro, login, etc) 
	 * e não se o usuário logado é que possui esta permissão (Salvo quando matricula e vinCodigo são nulos e servidorLogado != null)
	 * 
	 * 
	 * CUIDADO, somente deve ser passado o servidorLogado quando o AGH faz o mesmo comportamento.
	 * Na maior parte das chamadas encontradas para esta função, o AGH passa:
	 * MCOC_BUSCA_CONS_PROF(colunaMatriculaEmTabelaX, colunaVinCodigoEmTabelaX)
	 * Nestes casos, você deve chamar esta função passando o servidorLogado NULO. 
	 * Porque você precisa validar a permissão da matricula e vinculo passados e não do servidorLogado.
	 * 
	 * @param serMatricula, serVinCodigo
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public String formataNomeProf(Integer serMatricula, Short serVinCodigo) throws ApplicationBusinessException {
		List<RapQualificacao> qualificacoes = getProntuarioOnlineFacade().buscarQualificacoesDoProfissional(serMatricula, serVinCodigo);
		if(qualificacoes == null || qualificacoes.isEmpty()){
			return null;
		}
        String nomeProfissional = getProntuarioOnlineFacade().obterNomeProfissionalQ(serMatricula, serVinCodigo, qualificacoes);
		return nomeProfissional;
	}

	private List<LinhaReportVO> processarQAef(Integer pacCodigo, Short gsoSeqp, Byte seqp,
			RelExameFisicoRecemNascidoVO vo) {
		
		List<LinhaReportVO> listaSubReport = new ArrayList<LinhaReportVO>();
		
		List<McoAchadoExameFisicos> qAefList = getPerinatologiaFacade()
				.listarAchadosExamesFisicosPorCodigoPacienteGsoSeqpSepq(pacCodigo, gsoSeqp, seqp);
		
		for (McoAchadoExameFisicos achadoExameFisicos : qAefList) {
			LinhaReportVO linha = new LinhaReportVO();
			McoAchado qAcd = processarQAcd(achadoExameFisicos.getId().getAcdSeq());
			if(qAcd != null && qAcd.getAelRegiaoAnatomica() != null){
				linha.setTexto1(qAcd.getAelRegiaoAnatomica().getDescricao());
			}
			if(qAcd != null){
				linha.setTexto2(qAcd.getDescricao());
			}
			linha.setTexto3(achadoExameFisicos.getComplAchado());
			listaSubReport.add(linha);
		}			
		return listaSubReport;
	}

	private McoAchado processarQAcd(Integer acdSeq) {
		McoAchado mcoAchado = getPerinatologiaFacade().obterMcoAchadoObterPorChavePrimaria(acdSeq);
		return mcoAchado;
	}

	private void processarQSdm(Integer sindromeSeq,
			RelExameFisicoRecemNascidoVO vo) {
		McoSindrome qSdm = getPerinatologiaFacade().obterMcoSindromePorChavePrimaria(sindromeSeq);
		vo.setDescricao(qSdm.getDescricao());		
	}

	private void processarQEfr(Integer pacCodigo, Short gsoSeqp,
			Byte seqp, RelExameFisicoRecemNascidoVO vo) {
		McoExameFisicoRns qEfr = getPerinatologiaFacade().obterMcoExameFisicoRnsPorChavePrimaria(pacCodigo, gsoSeqp, seqp);
		if(qEfr != null){
			if(qEfr.getPerCefalico() != null){
				vo.setPerCefalico(qEfr.getPerCefalico().setScale(1, RoundingMode.FLOOR));
			}
			if(qEfr.getPerToracico() != null){
				vo.setPerToraxico(qEfr.getPerToracico().setScale(1, RoundingMode.FLOOR));
			}
			if(qEfr.getCirAbdominal() != null){
				vo.setCirAbdominal(qEfr.getCirAbdominal().setScale(1, RoundingMode.FLOOR));
			}
			if (qEfr.getAspectoGeral() != null){
				vo.setAspectoGeral(qEfr.getAspectoGeral().getDescricao());
			}
			vo.setAdequacaoPeso(qEfr.getAdequacaoPeso());
			vo.setIgFinal(qEfr.getIgFinal());
			vo.setTemperatura(formataTemperatura(qEfr.getTemperatura()));
			vo.setFreqCardiaca(qEfr.getFreqCardiaca());
			vo.setFreqRespiratoria(qEfr.getFreqRespiratoria());
			vo.setSindromeSeq(qEfr.getSdmSeq());
			
			processarQEfrOutros(qEfr, vo);
		}
		
	}

	private void processarQEfrOutros(McoExameFisicoRns qEfr, RelExameFisicoRecemNascidoVO vo) {
		if(qEfr.getIndSemParticularidades().equalsIgnoreCase("S")){
			vo.setParticularidades("SEM PARTICULARIDADES AO PRIMEIRO EXAME");
		}else if (qEfr.getIndSemParticularidades().equalsIgnoreCase("N")) {
			vo.setParticularidades("PARTICULARIDADES A RELATAR");
		}else{
			vo.setParticularidades(null);
		}
		if (qEfr.getMoro() != null){
			vo.setMoro(qEfr.getMoro().getDescricao());
		}
		if(qEfr.getFugaAAsfixia() != null){
			vo.setFugaAsfixia(qEfr.getFugaAAsfixia().getDescricao());
		}
		if(qEfr.getReptacao() != null){
			vo.setReptacao(qEfr.getReptacao().getDescricao());
		}
		if(qEfr.getMarcha() != null){
			vo.setMarcha(qEfr.getMarcha().getDescricao());
		}
		if(qEfr.getSuccao() != null){
			vo.setSuccao(qEfr.getSuccao().getDescricao());
		}
		if (qEfr.getReflexoVermelho() != null){
			vo.setReflexoVermelho(qEfr.getReflexoVermelho().getDescricao());
		}
		vo.setObservacao(qEfr.getObservacao());

		
	}

	private String formataTemperatura(BigDecimal temperatura) {
		String temperaturaFormatada = null;
		if (temperatura != null) {
			StringBuilder temperatura2 = new StringBuilder();
			temperatura2.append(
					temperatura.setScale(1, RoundingMode.DOWN).doubleValue())
					.append(" °C");
			temperaturaFormatada = temperatura2.toString().replace(".", ",");
		}
		return temperaturaFormatada;
	}

	private void processarQAtp(RelExameFisicoRecemNascidoVO vo) {
		AipAlturaPacientes qAtp = getPacienteFacade().obterAlturasPaciente(vo.getRnaPacCodigo(), DominioMomento.N);
		if(qAtp != null){
			vo.setAltura(qAtp.getAltura().setScale(1, RoundingMode.FLOOR));
		}
	}

	private void processarQPep(RelExameFisicoRecemNascidoVO vo) {
		AipPesoPacientes qPep = getPacienteFacade().obterPesoPaciente(vo.getRnaPacCodigo(), DominioMomento.N);
		if(qPep != null){
			vo.setPeso((qPep.getPeso().multiply(BigDecimal.valueOf(1000L)).setScale(0, RoundingMode.DOWN).toString().concat(" g")));
		}
	}

	private void processarMcocVerConv(Integer pacCodigo, Integer conNumero, RelExameFisicoRecemNascidoVO vo) {
		String mcocVerConv = getProntuarioOnlineFacade().obterDescricaoConvenio(pacCodigo, conNumero);
		vo.setConvenioMae(mcocVerConv);
	}
	
	private void processarQPeso(Integer conNumero, RelExameFisicoRecemNascidoVO vo) {
		AipPesoPacientes qPeso = getPacienteFacade().obterPesoPacientesPorNumeroConsulta(conNumero);
		if(qPeso != null){
			StringBuilder peso = new StringBuilder();
			if (qPeso.getPeso().scale() > 0) {
				
				peso.append(qPeso.getPeso()
							.setScale(1, RoundingMode.DOWN).doubleValue()).append(" kg");
			} else {
				peso.append(qPeso.getPeso().toString()).append(" kg");
			}
			vo.setPesoMae(peso.toString().replace(".",","));
		}
	}

	private void processarQAltura(Integer conNumero, RelExameFisicoRecemNascidoVO vo) {
		
		AipAlturaPacientes qAltura = getPacienteFacade().obterAlturaPorNumeroConsulta(conNumero);
		if(qAltura != null && qAltura.getAltura() != null){
			StringBuilder sb = new StringBuilder();
			sb.append(AghuNumberFormat.formatarNumeroMoeda(qAltura.getAltura().divide(new BigDecimal(100),2,BigDecimal.ROUND_CEILING).doubleValue())).append(" m");
			vo.setAlturaMae(sb.toString());
		}
	}

	private void processarQPacRn(Integer rnaPacCodigo, RelExameFisicoRecemNascidoVO vo, Integer pacCodigo, Short gsoSeqp, Byte seqp) {
		AipPacientes qPacRn = getPacienteFacade().obterPacientePorCodigo(rnaPacCodigo);
		vo.setNome(qPacRn.getNome());
		vo.setProntuario(CoreUtil.formataProntuarioRelatorio(qPacRn.getProntuario()));
		vo.setNascimento(DateUtil.dataToString(qPacRn.getDtNascimento(), "dd/MM/yyyy HH:mm"));
		if(qPacRn.getSexo() != null){
			vo.setSexo(qPacRn.getSexo().getDescricao());
		}else{
			vo.setSexo(DominioSexo.M.getDescricao());
		}
		
		McoRecemNascidos qRecemNascido = getPerinatologiaFacade().obterRecemNascidoPorPacCodigoGsoSeqpSeqp(pacCodigo, gsoSeqp, seqp);
		vo.setIdadeMae(getAmbulatorioFacade().obterIdadeMesDias(vo.getDataNascimentoMae(), qRecemNascido.getCriadoEm()));
	}

	private void processarQRecemNascido(Integer pacCodigo, Short gsoSeqp,
			Byte seqp, RelExameFisicoRecemNascidoVO vo) {
		McoRecemNascidos qRecemNascido = getPerinatologiaFacade().obterRecemNascidoPorPacCodigoGsoSeqpSeqp(pacCodigo, gsoSeqp, seqp);
		vo.setRnaPacCodigo(qRecemNascido.getPaciente().getCodigo());
		if(qRecemNascido.getMcoExameFisicoRns() != null){
			if(qRecemNascido.getMcoExameFisicoRns().getTempoRetornoMae() != null){
				vo.setTempoRetornoMae(qRecemNascido.getMcoExameFisicoRns().getTempoRetornoMae().toString().concat(" h"));
			}
			if(qRecemNascido.getMcoExameFisicoRns().getTempoRetornoMin() != null){
				vo.setTempoRetornoMin(qRecemNascido.getMcoExameFisicoRns().getTempoRetornoMin().toString().concat(" min"));
			}else{
				vo.setTempoRetornoMin("");
			}
		}
		
		if(qRecemNascido.getIndCrede()){
			vo.setCrede("CREDE");
		}
		
		
		AipPacientes pacienteRecemNascido = pacienteDAO.obterPorChavePrimaria(qRecemNascido.getPaciente().getCodigo());
		
		if(pacienteRecemNascido.getProntuario() != null &&  pacienteRecemNascido.getProntuario() > VALOR_MAXIMO_PRONTUARIO){
			vo.setProntuarioProvisorio(CoreUtil.formataProntuarioRelatorio(pacienteRecemNascido.getProntuario()));
			vo.setProntuarioProvisorioMae("MÃE: ".concat(vo.getProntuarioMae()));
		}else{
			if(pacienteRecemNascido.getProntuario() != null){
				vo.setProntuarioProvisorio(CoreUtil.formataProntuarioRelatorio(pacienteRecemNascido.getProntuario()));
			}else{
				vo.setProntuarioProvisorioMae(vo.getProntuarioMae());
			}
		}
	}

	private void processarQPac(Integer pacCodigo, Short gsoSeqp,
			Integer conNumero, RelExameFisicoRecemNascidoVO vo) {
		McoAnamneseEfs qPac = getPerinatologiaFacade().obterAnamnesePorPacienteGsoSeqpConNumero(pacCodigo, gsoSeqp, conNumero);
		
		vo.setNomeMae(qPac.getPaciente().getNome());
		vo.setProntuarioMae(CoreUtil.formataProntuarioRelatorio(qPac.getPaciente().getProntuario()));
		vo.setDtAtendimentoMae(qPac.getDthrConsulta());
		if(qPac.getPaciente().getDtUltAlta() == null || CoreUtil.isMenorDatas(qPac.getPaciente().getDtUltAlta(), qPac.getPaciente().getDtUltInternacao())){
			vo.setLeito(qPac.getPaciente().getLtoLtoId());
		}
		vo.setDataNascimentoMae(qPac.getPaciente().getDtNascimento()); 
	}
	
	/*public Boolean habilitarBotaoExamefisicoRN(Integer pacCodigo, Integer conNumero){
		AghAtendimentos atendimento = getAghuFacade().obterAtendimentoPorCodigoPacienteNumeroConsulta(pacCodigo, conNumero);
		if(atendimento != null && atendimento.getGsoSeqp() != null){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}*/
	
	public List<McoRecemNascidos> listarSeqpRecemNascido(Integer pacCodigo, Integer conNumero){
		//Obtém resultado da C3
		AghAtendimentos atendimento = getAghuFacade().obterAtendimentoPorCodigoPacienteNumeroConsulta(pacCodigo, conNumero);
		Short gsoSeqp = atendimento.getGsoSeqp();
		
		List<McoRecemNascidos> listMcoRecemNascidos = getPerinatologiaFacade().pesquisarMcoRecemNascidoPorGestacaoOrdenado(pacCodigo, gsoSeqp);
		
		if(listMcoRecemNascidos != null && listMcoRecemNascidos.size() > 0){
			return listMcoRecemNascidos;
		}
		return null;
	}
	
	protected IPerinatologiaFacade getPerinatologiaFacade(){
		return perinatologiaFacade;
	}
	
	protected IPacienteFacade getPacienteFacade(){
		return pacienteFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade(){
		return ambulatorioFacade;
	}
	
	protected IProntuarioOnlineFacade getProntuarioOnlineFacade(){
		return prontuarioOnlineFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	protected IAghuFacade getAghuFacade(){
		return aghuFacade;
	}
	
}
