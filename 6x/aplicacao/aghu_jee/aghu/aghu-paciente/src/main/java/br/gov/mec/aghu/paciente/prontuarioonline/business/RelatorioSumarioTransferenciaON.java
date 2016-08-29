package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.MpmAltaMotivo;
import br.gov.mec.aghu.model.MpmAltaOtrProcedimento;
import br.gov.mec.aghu.model.MpmAltaPlano;
import br.gov.mec.aghu.model.MpmAltaRecomendacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmTrfDestino;
import br.gov.mec.aghu.model.MpmTrfDiagnostico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.RelatorioSumarioTransferenciaVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class RelatorioSumarioTransferenciaON extends BaseBusiness {

	private static final String _NL = " \n";

	@EJB
	private RelExameFisicoRecemNascidoPOLON relExameFisicoRecemNascidoPOLON;
	
	private static final Log LOG = LogFactory.getLog(RelatorioSumarioTransferenciaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IControlePacienteFacade controlePacienteFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1196364234222082625L;

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public RelatorioSumarioTransferenciaVO criarRelatorioSumarioTransferencia(AghAtendimentos atendimentoTransf, String  tipoImpressao, Short seqpAltaSumario) throws ApplicationBusinessException{
		
		List<MpmAltaSumario> altas = getPrescricaoMedicaFacade().pesquisarAltaSumarioTransferencia(atendimentoTransf, seqpAltaSumario);//atendimento.getAltasSumario();
		
		MpmAltaSumario altaSumario = altas.get(0);//mpmAltaSumarioDAO;
		//MpmAltaSumarioId atendimento = altaSumario.getId();
		
		RelatorioSumarioTransferenciaVO relSumario = new RelatorioSumarioTransferenciaVO();
		
		if(altaSumario.getDthrElaboracaoTransf() != null){
			relSumario.setDataRopape(altaSumario.getDthrElaboracaoTransf());
		}else{
			relSumario.setDataRopape(new Date());
		}
		
		relSumario.setpAsuAtdSeq(altaSumario.getId().getApaAtdSeq());
		relSumario.setpAsuApaSeq(altaSumario.getId().getApaSeq());
		relSumario.setpAsuSeq(altaSumario.getId().getSeqp().intValue());
		
		//relSumario.setTipoImpressao(DominioTipoImpressaoRelatorio.valueOf(tipoImpressao));
		
		//identificação
		relSumario.setNomePaciente(altaSumario.getNome());
		relSumario.setIdade(criaIdadeExtenso(altaSumario.getIdadeAnos(),altaSumario.getIdadeMeses(),altaSumario.getIdadeDias()));
		relSumario.setSexo(altaSumario.getSexo().getDescricao());
		relSumario.setDataInternacao(altaSumario.getDthrAtendimento());
		relSumario.setDataTransferencia(altaSumario.getDthrAlta());
		relSumario.setProntuario(CoreUtil.formataProntuarioRelatorio(altaSumario.getProntuario()));
		
		if (altaSumario.getLeito() != null) {
			relSumario.setLeito(altaSumario.getLeito().getLeitoID());	
		} else {
			relSumario.setLeito(" ");
		}
		
		relSumario.setPermanencia(altaSumario.getDiasPermanencia() + " dias");	
		relSumario.setConvenio(altaSumario.getDescConvenio());
		relSumario.setEquipeResponsavel(altaSumario.getDescEquipeResponsavel());
		relSumario.setServicoResponsavel(altaSumario.getDescServicoResponsavel());	
		relSumario.setIndetificador(altaSumario.getId().toString());

		Integer matriculaValida = altaSumario.getServidorValida() != null ? altaSumario.getServidorValida().getId().getMatricula() : null;
		Short 	vinCodigoValida = altaSumario.getServidorValida() != null ? altaSumario.getServidorValida().getId().getVinCodigo() : null;
		relSumario.setNomeSiglaRegistro(buscarNomeSiglaRegistro(matriculaValida, vinCodigoValida));
		
		//relSumario.setDthrElaboracaoObito(altaSumario.getDthrElaboracaoAlta());

		relSumario.setOutrasEquipes(buscarOutrasEquipes(altaSumario.getId()));

		Integer indice = 1;
		
		//transferência - destino
		MpmTrfDestino destino = getPrescricaoMedicaFacade().obterEquipeDestino(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
		
		relSumario.setEquipeDestino(destino.getDescEquipe());
		relSumario.setEspecialidadeDestino(destino.getDescEsp());
		relSumario.setUnidadeDestino(destino.getDescUnidade());
		relSumario.setInstituicaoDestino(destino.getDescInstituicao());
		
		processarDestinoConcatenado(relSumario);
		
		//diagnósticos
		relSumario.setMotivoTransferencia(buscarMotivoTransferencia(altaSumario.getId()));
		relSumario.setDiagnosticosPrincipais(buscarTransferenciaDiagPrincipais(altaSumario.getId()));
        relSumario.setDiagnosticoSecundario(buscarTransferenciaDiagSecundario(altaSumario.getId()));
        
		
		if ((relSumario.getMotivoTransferencia() != null && !relSumario.getMotivoTransferencia().isEmpty()) 
		  	 || (relSumario.getDiagnosticosPrincipais() != null && !relSumario.getDiagnosticosPrincipais().isEmpty())
		  	 || (relSumario.getDiagnosticoSecundario() !=null && !relSumario.getDiagnosticoSecundario().isEmpty())) {
			
			relSumario.setIndiceDiagnostico(++indice + ".");
			
		}
        
        //procedimentos diagnósticos ou terapêuticos e cirurgias
		relSumario.setOutrosProcedimentos(buscarOutrosProcedimentos(altaSumario.getId()));
		relSumario.setCirurgiasRealizadas(buscarAltaCirgRealizadas(altaSumario.getId()));
		relSumario.setConsultorias(buscarAltaConsultorias(altaSumario.getId()));
		relSumario.setPrincipaisFarmacos(buscarPrincFarmacos(altaSumario.getId()));   
		relSumario.setComplementoFarmacos(buscarComplFarmacos(altaSumario.getId()));

		if ((relSumario.getCirurgiasRealizadas()!=null && !relSumario.getCirurgiasRealizadas().isEmpty())
				||(relSumario.getOutrosProcedimentos()!=null && !relSumario.getOutrosProcedimentos().isEmpty())
				||(relSumario.getConsultorias()!=null && !relSumario.getConsultorias().isEmpty())
				||(relSumario.getPrincipaisFarmacos()!=null && !relSumario.getPrincipaisFarmacos().isEmpty())
				||(relSumario.getComplementoFarmacos()!=null && !relSumario.getComplementoFarmacos().isEmpty())){
				
			relSumario.setIndiceProcedTerapeutico(++indice + ".");
		}
		
		
		//evolução
		relSumario.setEvolucao(buscarEvolucao(altaSumario.getId()));
		
		if (relSumario.getEvolucao() != null){
			relSumario.setIndiceEvolucao(++indice + ".");
		}
		

		//plano terapeutico
		relSumario.setMotivoAlta(buscarAltaMotivo(altaSumario.getId()));
		relSumario.setPlanoPosAlta(buscarPlanoPosAlta(altaSumario.getId()));
		relSumario.setRecomendacoes(buscarAltaRecomendacoes(altaSumario.getId()));
		
		if ((relSumario.getMotivoAlta()!=null && !relSumario.getMotivoAlta().isEmpty())
				||(relSumario.getPlanoPosAlta()!=null && !relSumario.getPlanoPosAlta().isEmpty())
				||(relSumario.getRecomendacoes()!=null && !relSumario.getRecomendacoes().isEmpty())){

			relSumario.setIndicePlanoTerapeutico(++indice + ".");
		}

		processarPlanoTerapeuticoConcatenado(relSumario);
		//identificação rodape
		
		// paciente recém nacido requer prontuário da mãe no relatório
		relSumario.setProntuarioRodape(CoreUtil.formataProntuarioRelatorio(altaSumario.getProntuario()));
		relSumario.setNomeRodape(altaSumario.getPaciente().getNome());
//		if (altaSumario.getProntuario() != null
//				&& altaSumario.getProntuario() > 90000000) {
//
//			if (altaSumario.getPaciente().getMaePaciente() != null) {
//				relSumario.setProntuarioRodape(CoreUtil
//						.formataProntuarioRelatorio(altaSumario
//								.getPaciente().getProntuario())
//						+ "          Mãe: "
//						+ CoreUtil.formataProntuarioRelatorio(altaSumario.getPaciente()
//								.getMaePaciente().getProntuario()));
//			}
//		}
				
		
		relSumario.setLocalRodape(obterLocalizacaoPacienteParaRelatorio(altaSumario.getLeito(), altaSumario.getQuarto(), altaSumario.getUnidadeFuncional()));
		
		return relSumario;
	}
	
	/**
	 * Refactor da outra função "mpmc_localiza_pac" pois ela recebia parametros diferentes
	 * Na utilização da função com MamAltaSumario o resultado é diferente.
	 * 
	 * Caso seja necessário utilizar utilizar a função abaixo desta.
	 * @ORADB mpmc_localiza_pac
	 * @param leito
	 * @param quarto
	 * @param unidadeFuncional
	 * @return
	 *  
	 */
	public String obterLocalizacaoPacienteParaRelatorio(String leitoID, Short numeroQuarto, Short seqUnidadeFuncional){
		String local = null;
		
		if (leitoID != null) {
			local = "Leito: " + leitoID;
		} else if (numeroQuarto != null) {
			local = "Quarto: " + numeroQuarto;
		} else if (seqUnidadeFuncional != null) {
			AghUnidadesFuncionais unidadeFuncional = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(seqUnidadeFuncional);
			if(unidadeFuncional.getSigla() != null){
				local = "Unidade: " + unidadeFuncional.getSigla();
			}else if(unidadeFuncional.getLPADAndarAla() !=null){
				local = "Unidade: " + unidadeFuncional.getLPADAndarAla();
			}
		} 
		return local;
	}
	
	/**
	 * @ORADB mpmc_localiza_pac
	 * @param leito
	 * @param quarto
	 * @param unf
	 * @return
	 */
	public String obterLocalizacaoPacienteParaRelatorio(AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unf){
		String leitoId = leito != null ? leito.getLeitoID() : null;
		Short numeroQuarto = quarto != null ? quarto.getNumero() : null;
		Short seqUnf = unf != null ? unf.getSeq() : null;
		return obterLocalizacaoPacienteParaRelatorio(leitoId, numeroQuarto, seqUnf);
	}
	
	private void processarDestinoConcatenado(
			RelatorioSumarioTransferenciaVO relSumario) {
		StringBuilder texto = new StringBuilder();
		Boolean anterior = Boolean.FALSE;
		if(notNullNotBlank(relSumario.getEquipeDestino())){
			texto.append(relSumario.getEquipeDestino());
			anterior = Boolean.TRUE;
		}
		
		if(notNullNotBlank(relSumario.getEspecialidadeDestino())){
			if(anterior){
				texto.append(_NL);
			}
			texto.append(relSumario.getEspecialidadeDestino());
			anterior = Boolean.TRUE;
		}
		
		if(notNullNotBlank(relSumario.getUnidadeDestino())){
			
			if(anterior){
				texto.append(_NL);
			}
			texto.append(relSumario.getUnidadeDestino());
			anterior = Boolean.TRUE;
		}
		
		if(notNullNotBlank(relSumario.getInstituicaoDestino())){
			
			if(anterior){
				texto.append(_NL);
			}
			
			texto.append(relSumario.getInstituicaoDestino());
		}
		
		relSumario.setDestinoConcatenado(texto.toString());
	}
	
	private void processarPlanoTerapeuticoConcatenado(
			RelatorioSumarioTransferenciaVO relSumario) {
		StringBuilder texto = new StringBuilder();
		Boolean anterior = Boolean.FALSE;
		if(notNullNotBlank(relSumario.getMotivoAlta())){
			texto.append(relSumario.getMotivoAlta().toString());
			anterior = Boolean.TRUE;
		}
		
		if(notNullNotBlank(relSumario.getPlanoPosAlta())){
			if(anterior){
				texto.append(_NL);
			}
			texto.append(relSumario.getPlanoPosAlta().toString());
			anterior = Boolean.TRUE;
		}
		
		if(!(relSumario.getRecomendacoes().isEmpty())){
			if(anterior){
				texto.append(_NL);
			}
			for(int i = 0; i< relSumario.getRecomendacoes().size(); i++){
				LinhaReportVO vo = relSumario.getRecomendacoes().get(i);
				texto.append(vo.getTexto1());
				
				if(i+1 != relSumario.getRecomendacoes().size()){
					texto.append(_NL);	
				}
			}
		}

		relSumario.setPlanoTerapeuticoConcatenado(texto.toString());
		
	}

	private Boolean notNullNotBlank(String str){
		if(str != null && !str.trim().isEmpty()){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}

	private String buscarNomeSiglaRegistro(Integer matricula, Short vinculo) throws ApplicationBusinessException{
		/*BuscaConselhoProfissionalServidorVO vo = getProntuarioOnlineFacade().buscaConselhoProfissionalServidorVO(matricula, vinculo);
		return vo != null ? 
				substituiNullPorEspaco(vo.getNome(),true) + 
			   substituiNullPorEspaco(vo.getSiglaConselho(),true)+ 
			   substituiNullPorEspaco(vo.getNumeroRegistroConselho(),false)
			   : ""; */
		String retornoNomeSigla = getRelExameFisicoRecemNascidoPOLON().formataNomeProf(matricula, vinculo);
		if(StringUtils.isNotBlank(retornoNomeSigla)){
			return retornoNomeSigla;
		}
		/*BuscaConselhoProfissionalServidorVO vo = getProntuarioOnlineFacade().buscaConselhoProfissionalServidorVO(matricula, vinculo);
		if(StringUtils.isNotBlank(vo.getSiglaConselho()) && StringUtils.isNotBlank(vo.getNome())){
			return substituiNullPorEspaco(vo.getNome(),true) + 
				   substituiNullPorEspaco(vo.getSiglaConselho(),true)+ 
				   substituiNullPorEspaco(vo.getNumeroRegistroConselho(),false);
		}*/else{
			if(matricula != null && vinculo != null){
				return getRegistroColaboradorFacade().buscarServidor(vinculo, matricula).getPessoaFisica().getNome();
			}else{
				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
				
				return servidorLogado.getPessoaFisica().getNome();
			}
		}
	}	
	
	protected RelExameFisicoRecemNascidoPOLON getRelExameFisicoRecemNascidoPOLON(){
		return relExameFisicoRecemNascidoPOLON;
	}
	public IProntuarioOnlineFacade getProntuarioOnlineFacade(){
		return prontuarioOnlineFacade;
	}
	
	private List<LinhaReportVO> buscarOutrasEquipes(final MpmAltaSumarioId id){
		return getPrescricaoMedicaFacade().obterOutrasEquipes(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		
	}

	private String buscarMotivoTransferencia(final MpmAltaSumarioId id) throws ApplicationBusinessException{
		return getPrescricaoMedicaFacade().obterMotivoTransferencia(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}
	
	private List<LinhaReportVO> buscarTransferenciaDiagPrincipais(final MpmAltaSumarioId id) throws ApplicationBusinessException{
		List<LinhaReportVO> listaLinhaReportVO = new ArrayList<LinhaReportVO>(); 
		
		List<MpmTrfDiagnostico> transferenciaDiag = getPrescricaoMedicaFacade().obterTransferenciaDiagPrincipais(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		
		
		for (MpmTrfDiagnostico obtTransferenciaDiag:transferenciaDiag) {
            //concatena Desc Cid e Complemento no Texto 1
			
			LinhaReportVO linhaReportVO = new LinhaReportVO(); 
			if (obtTransferenciaDiag.getDescCid() != null) {
				linhaReportVO.setTexto1(obtTransferenciaDiag.getDescCid());
			}
			
			if (obtTransferenciaDiag.getComplCid() != null) {
				linhaReportVO.setTexto1(linhaReportVO.getTexto1()+ " " +obtTransferenciaDiag.getComplCid());
			}
			
			listaLinhaReportVO.add(linhaReportVO);
			
		}

		return  listaLinhaReportVO;		

	}
	
	private String buscarTransferenciaDiagSecundario(final MpmAltaSumarioId id) throws ApplicationBusinessException{
		return getPrescricaoMedicaFacade().obterTransferenciaDiagSecundario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}
	
	private List<LinhaReportVO> buscarOutrosProcedimentos(final MpmAltaSumarioId id) throws ApplicationBusinessException{
		List<LinhaReportVO> listaLinhaReportVO = new ArrayList<LinhaReportVO>(); 
		
		List<MpmAltaOtrProcedimento> otrProcedimento = getPrescricaoMedicaFacade().obterMpmAltaOtrProcedimento(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		
		for (MpmAltaOtrProcedimento altaOtrProcedimento : otrProcedimento) {
			// concatena Procedimento e Outros Procedimentos
			LinhaReportVO linhaReportVO = new LinhaReportVO();
			linhaReportVO.setData(altaOtrProcedimento.getDthrOutProcedimento());
			
			if (altaOtrProcedimento.getComplOutProcedimento() != null) {
				linhaReportVO.setTexto1(altaOtrProcedimento
						.getComplOutProcedimento());
			} else if (altaOtrProcedimento.getDescOutProcedimento() != null) {
				linhaReportVO.setTexto1(altaOtrProcedimento
						.getDescOutProcedimento());
			}
			
			linhaReportVO.setTexto2(altaOtrProcedimento.getId().getAsuApaAtdSeq().toString());
			
			listaLinhaReportVO.add(linhaReportVO);

		}
		CoreUtil.ordenarLista(listaLinhaReportVO, "texto2", false);
		CoreUtil.ordenarLista(listaLinhaReportVO, "texto1", true);
		
		
		return  listaLinhaReportVO;
		
	}
	
	private List<LinhaReportVO> buscarAltaCirgRealizadas(final MpmAltaSumarioId id){
		
		List<LinhaReportVO> otrCirgRealizada = getPrescricaoMedicaFacade().obterAltaCirgRealizadas(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());

		CoreUtil.ordenarLista(otrCirgRealizada, "texto1", false);
		CoreUtil.ordenarLista(otrCirgRealizada, "data", true);
		
		return  otrCirgRealizada;
		
	}	
	
	private List<LinhaReportVO> buscarAltaConsultorias(final MpmAltaSumarioId id){
		return getPrescricaoMedicaFacade().obterAltaConsultorias(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}		
	
	private List<LinhaReportVO> buscarPrincFarmacos(final MpmAltaSumarioId id){
		return getPrescricaoMedicaFacade().obterPrincFarmacos(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}		
	
	private List<LinhaReportVO> buscarComplFarmacos(final MpmAltaSumarioId id){
		return getPrescricaoMedicaFacade().obterComplFarmacos(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}		

	private String buscarEvolucao(final MpmAltaSumarioId id){
		return getPrescricaoMedicaFacade().obterEvolucao(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}		
	
	private String buscarAltaMotivo(final MpmAltaSumarioId id) throws ApplicationBusinessException{
		MpmAltaMotivo altaMotivo = getPrescricaoMedicaFacade().obterMpmAltaMotivo(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		String motivo = null;
		
		if(altaMotivo == null){
			motivo = null;
		}else{
			if(altaMotivo.getMotivoAltaMedicas().getIndOutros() == Boolean.TRUE){
				motivo = altaMotivo.getComplMotivo();
			}else{
				
				motivo = altaMotivo.getDescMotivo() != null ? altaMotivo.getDescMotivo() : "";
				motivo = new StringBuffer().append(altaMotivo.getComplMotivo() != null ? motivo + " " + altaMotivo.getComplMotivo() : motivo).toString();
				//motivo = new StringBuffer(altaMotivo.getComplMotivo() != null ? motivo + " " + altaMotivo.getComplMotivo() : "").toString();
			}
		}
		return motivo;
	}
	
	private String buscarPlanoPosAlta(final MpmAltaSumarioId id) throws ApplicationBusinessException{
		MpmAltaPlano planoPosAlta = getPrescricaoMedicaFacade().obterMpmAltaPlano(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		String plano = null;

		if(planoPosAlta == null){
			plano = null;
		}else{
			if(planoPosAlta.getDescPlanoPosAlta() == null && planoPosAlta.getComplPlanoPosAlta() == null){
				plano = null;
			}else{
				if (planoPosAlta.getMpmPlanoPosAltas().getIndOutros() == DominioSimNao.S){
					plano = planoPosAlta.getComplPlanoPosAlta();
				}else{
					plano = planoPosAlta.getDescPlanoPosAlta() != null ? planoPosAlta.getDescPlanoPosAlta() : "";
					plano = new StringBuffer().append(planoPosAlta.getComplPlanoPosAlta() != null ? plano + " " + planoPosAlta.getComplPlanoPosAlta() : plano).toString();
			              
				}
			}
		}
	
		return plano;
	}	
	
	private List<LinhaReportVO> buscarAltaRecomendacoes(final MpmAltaSumarioId id) throws ApplicationBusinessException{
		
		List<LinhaReportVO> listaLinhaReportVO = new ArrayList<LinhaReportVO>();
		
		DominioSituacao situacao = DominioSituacao.A;
		
		List<MpmAltaRecomendacao> altaRecomendacao = getPrescricaoMedicaFacade().obterMpmAltaRecomendacoes(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp(), situacao);
		
		
		for (MpmAltaRecomendacao obtAltaRecomendacao:altaRecomendacao) {
            //concatena Desc Cid e Complemento no Texto 1
			
			LinhaReportVO linhaReportVO = new LinhaReportVO(); 
			if (obtAltaRecomendacao.getDescricao() != null) {
				linhaReportVO.setTexto1(obtAltaRecomendacao.getDescricao());
			}
					
			listaLinhaReportVO.add(linhaReportVO);
			
		}

		return  listaLinhaReportVO;	
	}	
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	private String criaIdadeExtenso(Short anos, Integer meses, Integer dias){		
		StringBuffer idadeExt = new StringBuffer();
		if (anos!=null && anos > 1) {
			idadeExt.append(anos).append(" anos ");
		}else if (anos!=null && anos == 1) {
			idadeExt.append(anos).append(" ano ");
		}
		if (meses!=null && meses > 1) {
			idadeExt.append(meses).append(" meses ");
		}else if (meses!=null && meses == 1) {
			idadeExt.append(meses).append(" mês ");
		}
		if (anos==null || anos == 0){ 
			if (dias!=null && dias > 1) {
				idadeExt.append(dias).append(" dias ");
			}else if (dias!=null && dias == 1) {
				idadeExt.append(dias).append(" dia ");
			}
		}		
		return idadeExt.toString();
	}
	
	/*private String substituiNullPorEspaco(String str, boolean addEspaco){
		if (str==null){
			return "";
		}else{
			if (addEspaco){
				return str+" ";
			}else{
				return str;
			}	
		}
	}*/
	
	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	public IFarmaciaFacade getFarmaciaFacade(){
		return this.farmaciaFacade;
	}
	
	public IControlePacienteFacade getControlePacienteFacade(){
		return controlePacienteFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
