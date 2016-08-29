package br.gov.mec.aghu.ambulatorio.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultaProcedHospitalarDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacFormaAgendamentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacHorarioGradeConsultaDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacRetornosDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacSituacaoConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.VAacConvenioPlanoDAO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultasRelatorioAgendaConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.DataInicioFimVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAgendamentoConsultaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioSituacaoAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacConsultasJn;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacFormaAgendamentoId;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacHorarioGradeConsulta;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AghCaractEspecialidadesId;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipGrupoFamiliarPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAacConvenioPlano;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaComplementoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
/**
 * 
 * @author tiago.felini
 * 
 */
@Stateless
public class MarcacaoConsultaON extends BaseBusiness {

    @EJB
    private AmbulatorioConsultaRN ambulatorioConsultaRN;

    private static final Log LOG = LogFactory.getLog(MarcacaoConsultaON.class);

    @Override
    @Deprecated
    protected Log getLogger() {
    	return LOG;
    }

    @EJB
    private IFaturamentoFacade faturamentoFacade;

    @Inject
    private AacSituacaoConsultasDAO aacSituacaoConsultasDAO;

    @Inject
    private AacConsultaProcedHospitalarDAO aacConsultaProcedHospitalarDAO;

    @Inject
    private AacConsultasDAO aacConsultasDAO;

    @EJB
    private IAmbulatorioFacade ambulatorioFacade;

    @Inject
    private AacRetornosDAO aacRetornosDAO;

    @Inject
    private AacFormaAgendamentoDAO aacFormaAgendamentoDAO;

    @Inject
    private VAacConvenioPlanoDAO vAacConvenioPlanoDAO;

    @EJB
    private IPacienteFacade pacienteFacade;

    @EJB
    private IParametroFacade parametroFacade;

    @EJB
    private IAghuFacade aghuFacade;

    @EJB
    private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

    @EJB
    private PesquisarPacientesAgendadosON pesquisarPacientesAgendadosON;

    @EJB
    private IServidorLogadoFacade servidorLogadoFacade;
    
    @Inject
	private AacHorarioGradeConsultaDAO aacHorarioGradeConsultaDAO;

	@Inject
	private AacGradeAgendamenConsultasDAO gradeAgendamentoConsultasDAO;

    private static final long serialVersionUID = -3369745783923859833L;

    public enum MarcacaoConsultaONExceptionCode implements BusinessExceptionCode {

		AAC_00728, AAC_00158, AAC_00778, AAC_00612, DIA_BLOQUEADO_CONSULTA_EXCEDENTE, NAO_EXISTE_CONSULTA_DATA_TURNO, AAC_00688, AAC_00166, AAC_00707, MARCACAO_PACIENTE_SEM_NUMERO_CARTAO_SUS, 
		MARCACAO_CONSULTA_PACIENTE_SEM_ENDERECO, MENSAGEM_CONVENIO_OBRIGATORIO, MENSAGEM_FORMA_AGENDAMENTO_OBRIGATORIO, PACIENTE_BLOQUEADO_UBS, CONSULTA_JA_MARCADA,ERRO_MARCAR_FALTA ,ENDERECO_NAO_EXISTENTE_PREJUDICA_FATURAMENTO;
	
		public void throwException(Object... params) throws ApplicationBusinessException {
		    throw new ApplicationBusinessException(this, params);
		}

    }

    public AipPacientes obterPacienteConsulta(AacConsultas consulta) {
		AipPacientes paciente = null;
	
		if (consulta.getComplemento() != null) {
		    if (consulta.getComplemento().getPacCodigo() != null) {
			paciente = getPacienteFacade().obterPacientePorCodigo(consulta.getComplemento().getPacCodigo());
		    } else if (consulta.getComplemento().getProntuario() != null) {
			String prontuario = consulta.getComplemento().getProntuario().replace("/", "");
			paciente = getPacienteFacade().obterPacientePorProntuario(Integer.parseInt(prontuario.trim()));
		    }
		}
		return paciente;
    }

    public List<VAacConvenioPlano> getListaConvenios(String parametro) {
		VAacConvenioPlanoDAO dao = getVAacConvenioPlanoDAO();
		List<VAacConvenioPlano> result = dao.pesquisarConvenios(parametro);
		if (result == null || result.isEmpty()) {
		    result = dao.pesquisarConvenios(parametro);
		}
		return result;
    }

    public Long getListaConveniosCount(String parametro) {
		VAacConvenioPlanoDAO dao = getVAacConvenioPlanoDAO();
		return dao.pesquisarConveniosCount(parametro);
    }

    public AacSituacaoConsultas obterSituacaoMarcada() {
		AacSituacaoConsultas situacaoCOnsulta = getAacSituacaoConsultasDAO().obterSituacaoPorSiglaAtiva("M");
		return situacaoCOnsulta;
    }

    public AacConsultas manterConsulta(AacConsultas consultaAnterior, AacConsultas consulta, String nomeMicrocomputador, boolean cameFromInterconsultas)
	    throws NumberFormatException, BaseException {

    	consulta = this.validarBanco(consulta);
		this.validaProjeto(consulta);
		if (consultaAnterior == null) {
		    AacSituacaoConsultas situacaoConsulta = getAacSituacaoConsultasDAO().obterSituacaoConsultaPeloId("M");
		    consulta.setSituacaoConsulta(situacaoConsulta);
		    consulta = getAmbulatorioConsultaRN().inserirConsulta(consulta, true, nomeMicrocomputador, new Date(), false);
		} else {
			if (cameFromInterconsultas) {
				AacSituacaoConsultas situacaoConsulta = getAacSituacaoConsultasDAO().obterSituacaoConsultaPeloId("M");
			    consulta.setSituacaoConsulta(situacaoConsulta);
			}
		    consulta = getAmbulatorioConsultaRN().atualizarConsulta(consultaAnterior, consulta, true, nomeMicrocomputador, new Date(), false, true);
		}
	
		return consulta;
    }

    public void manterConsulta(AacConsultas consultaAnterior, AacConsultas consulta, final AacFormaAgendamentoId idFormaAgendamento,
	    final boolean emergencia, final String nomeMicrocomputador, boolean cameFromInterconsultas) throws BaseException {

		ConsultaComplementoVO complementoVO = consulta.getComplemento();
	
		final AipPacientes pac = pacienteFacade.obterAipPacientesPorChavePrimaria(complementoVO.getPacCodigo());
		AacConsultas cons = null;
		if (consulta.getNumero() != null) {
		    cons = aacConsultasDAO.obterPorChavePrimaria(consulta.getNumero());
		}
		if (consulta.getCodCentral() == null && complementoVO.getCodCentral() != null) {
		    consulta.setCodCentral(complementoVO.getCodCentral());
		} else {
		    complementoVO.setCodCentral(consulta.getCodCentral());
		}
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		consulta.setServidorMarcacao(servidorLogado);
	
		if (consulta.getPaciente() == null || !consulta.getPaciente().getCodigo().equals(pac.getCodigo())) {
		    consulta.setPaciente(pac);
		}
	
			final VAacConvenioPlano convenioPlano = obterConvenioAmbulatorio();
		if (convenioPlano != null && consulta.getConvenioSaudePlano() == null) {
				if (cons != null && cons.getConvenioSaudePlano() != null) {
					if (!cons.getConvenioSaudePlano().getId().getCnvCodigo().equals(convenioPlano.getId().getCnvCodigo())
							&& !cons.getConvenioSaudePlano().getId().getSeq().equals(convenioPlano.getId().getPlano())) {
						consulta.setConvenioSaudePlano(faturamentoFacade.obterConvenioSaudePlano(convenioPlano.getId().getCnvCodigo(), convenioPlano.getId().getPlano()));
					}
				} else {
					consulta.setConvenioSaudePlano(faturamentoFacade.obterConvenioSaudePlano(convenioPlano.getId().getCnvCodigo(), convenioPlano.getId().getPlano()));
				}
			}
		
		if (idFormaAgendamento != null) {
		    final AacFormaAgendamento formaAgendamento = aacFormaAgendamentoDAO.obterPorChavePrimaria(idFormaAgendamento);
	
		    if (formaAgendamento != null) {
			consulta.setFormaAgendamento(formaAgendamento);
			consulta.setPagador(formaAgendamento.getPagador());
			consulta.setTipoAgendamento(formaAgendamento.getTipoAgendamento());
			consulta.setCondicaoAtendimento(formaAgendamento.getCondicaoAtendimento());
		    }
		}
		consulta = manterConsulta(consultaAnterior, consulta, nomeMicrocomputador, cameFromInterconsultas);
	
		// Para não dar Lazy na controller
		LOG.info("EmiteTicket " + consulta.getGradeAgendamenConsulta().getEmiteTicket());
	
		if (emergencia) {
		    chegou(consulta, nomeMicrocomputador);
		}
    }
    
    public Boolean verificarConsultaJaMarcada(AacConsultas consulta){
    	Boolean retorno = false;
    	if (consulta.getNumero() != null){
    		AacSituacaoConsultas situacao = aacConsultasDAO.obterSituacaoConsulta(consulta.getNumero()); 
    		if (situacao != null){
    			if (!situacao.getSituacao().equals("L")){
    				 retorno = true;
    			}
    		}
    	}
    	return retorno;
    }

    public void chegou(AacConsultas consulta, String nomeMicrocomputador) throws BaseException {
    	AacRetornos retorno = this.getAmbulatorioFacade().obterRetorno(DominioSituacaoAtendimento.PACIENTE_ATENDIDO.getCodigo());
	
		consulta.setRetorno(retorno);
		getPesquisarPacientesAgendadosON().registraChegadaPaciente(consulta, nomeMicrocomputador);
    }

    public List<String> validarItensPreManterConsulta(final AacConsultas consulta) throws ApplicationBusinessException {
	
		List<String> msgs = new ArrayList<>();
	
		if (consulta.getComplemento() != null && consulta.getComplemento().getPacCodigo() != null) {
		    ConsultaComplementoVO complementoVO = consulta.getComplemento();
	
		    final AipPacientes pac = pacienteFacade.obterAipPacientesPorChavePrimaria(complementoVO.getPacCodigo());
	
		    if (pac != null && pac.getPrntAtivo() != null && !pac.getPrntAtivo().equals(DominioTipoProntuario.A)) {
		    	msgs.add(MarcacaoConsultaONExceptionCode.AAC_00166.toString());
		    }

		}
	
		boolean informaCartaoSus = false;
		if (consulta.getGradeAgendamenConsulta() != null) {
		    informaCartaoSus = ambulatorioFacade.verificarCaracEspecialidade(consulta.getGradeAgendamenConsulta().getEspecialidade(), DominioCaracEspecialidade.INFORMA_CARTAO_SUS);
		}
	
		if (consulta.getPaciente() != null) {
			if (informaCartaoSus && consulta.getPaciente().getNroCartaoSaude() == null) {
				msgs.add(MarcacaoConsultaONExceptionCode.AAC_00707.toString());
			}
		    Long qtEndPac = pacienteFacade.obterQuantidadeEnderecosPaciente(consulta.getPaciente());
		    
		    if (consulta.getPaciente().getNroCartaoSaude() == null) {
				msgs.add(MarcacaoConsultaONExceptionCode.MARCACAO_PACIENTE_SEM_NUMERO_CARTAO_SUS.toString());
			}
		    
		    if (Long.valueOf(0).equals(qtEndPac)) {
			msgs.add(MarcacaoConsultaONExceptionCode.MARCACAO_CONSULTA_PACIENTE_SEM_ENDERECO.toString());
	
		    } else {
			    	AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_VERIFICA_ENDERECO_PACIENTE_MARCACAO_CONSULTA);
			    		if (parametro.getValor().equals("S")) {
			ambulatorioFacade.verificarEnderecoPaciente(consulta.getPaciente().getCodigo());
			    		} else {
			    			msgs.add(MarcacaoConsultaONExceptionCode.ENDERECO_NAO_EXISTENTE_PREJUDICA_FATURAMENTO.toString());
			    		}
		    }
		}
	
		return msgs;
    }

    private VAacConvenioPlano obterConvenioAmbulatorio() throws ApplicationBusinessException {
		Short convenioPadrao = parametroFacade.buscarValorShort(AghuParametrosEnum.P_SEQ_CONVENIO_PADRAO_AMB_V1);
		Byte planoPadrao = parametroFacade.buscarValorByte(AghuParametrosEnum.P_SEQ_PLANO_PADRAO_AMB_V1);
		return ambulatorioFacade.obterConvenioPlanoPorId(convenioPadrao, planoPadrao);
    }

    public void verificarEnderecoPaciente(Integer codPac) throws ApplicationBusinessException {
		AipPacientes pac = pacienteFacade.obterAipPacientesPorChavePrimaria(codPac);
	
		boolean enderecoValido = false;
		StringBuffer camposAVerificar = new StringBuffer();
	
		for (AipEnderecosPacientes endereco : pac.getEnderecos()) {
		    if (endereco.getAipBairrosCepLogradouro() != null) {
			enderecoValido = true;
			break;
		    } else {
			if (endereco.getLogradouro() != null && (endereco.getCidade() != null || endereco.getAipCidade() != null)
				&& endereco.getCep() != null) {
			    enderecoValido = true;
			    break;
			} else {
			    if (endereco.getLogradouro() == null) {
				camposAVerificar.append("endereço");
			    }
			    if (endereco.getCidade() == null && endereco.getAipCidade() == null) {
				if (camposAVerificar.length() > 0) {
				    camposAVerificar.append(", ");
				}
				camposAVerificar.append("cidade");
			    }
			    if (endereco.getCep() == null) {
				if (camposAVerificar.length() > 0) {
				    camposAVerificar.append(", ");
				}
				camposAVerificar.append("cep");
			    }
			}
		    }
		}
	
		if (!enderecoValido) {
		    MarcacaoConsultaONExceptionCode.AAC_00688.throwException(camposAVerificar.toString());
		}
    }

    public void verificarConsultaExcedenteDiaBloqueado(AacConsultas consulta) throws ApplicationBusinessException{
		boolean diaBloqueado = true;
		List<AacConsultas> listaConsultasDia = getAacConsultasDAO().pesquisarConsultasPorGrade(consulta.getGradeAgendamenConsulta().getSeq(), consulta.getDtConsulta());
		if (listaConsultasDia.isEmpty()){
			diaBloqueado = false;
		}
		else{
			for (AacConsultas con: listaConsultasDia){
				if (con.getSituacaoConsulta() == null || con.getSituacaoConsulta().getBloqueio() == null
						|| !con.getSituacaoConsulta().getBloqueio()){
					diaBloqueado = false;
					break;
				}
			}			
		}
		if (diaBloqueado){
			throw new ApplicationBusinessException(MarcacaoConsultaONExceptionCode.DIA_BLOQUEADO_CONSULTA_EXCEDENTE);
		}
	}

	public void verificarExisteConsultaMesmoDiaTurno(AacConsultas consulta) throws ApplicationBusinessException{
		List<ConsultasRelatorioAgendaConsultasVO> listaConsultasDia = this.getGradeAgendamentoConsultasDAO().obterConsultasGradeAgendamentoRelatorioAgenda(
				consulta.getDtConsulta(),consulta.getGradeAgendamenConsulta().getSeq(),getPesquisarPacientesAgendadosON().defineTurno(consulta.getDtConsulta()).getCodigo(),
				null, consulta.getGradeAgendamenConsulta().getSiglaUnfSala().getId().getUnfSeq(), consulta.getGradeAgendamenConsulta().getSiglaUnfSala().getId().getSala());
		
		if (listaConsultasDia == null || listaConsultasDia.isEmpty()){
			throw new ApplicationBusinessException(MarcacaoConsultaONExceptionCode.NAO_EXISTE_CONSULTA_DATA_TURNO);
		}
	}

    public Boolean verificarConsultaDiaNaoProgramado(AacConsultas consulta){
		DominioDiaSemana diaConsulta = CoreUtil.retornaDiaSemana(consulta.getDtConsulta());
		Boolean diaNaoProgramado = true;
		List<AacHorarioGradeConsulta> listaHorarios = getAacHorarioGradeConsultaDAO().pesquisarHorariosGradeConsulta(consulta.getGradeAgendamenConsulta().getSeq());
		for (AacHorarioGradeConsulta horario: listaHorarios){
			if (diaConsulta.equals(horario.getDiaSemana())){
				diaNaoProgramado = false;
			}
		}
		return diaNaoProgramado;
	}
	
	public String obterCabecalhoListaConsultasPorGrade(AacGradeAgendamenConsultas grade){
		String parametroZona = "";
		try{
			AghParametros p1 = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA);
			String zona = p1 != null ? p1.getVlrTexto() : "Zona";
			AghParametros p2 = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA);
			String sala = p2 != null ? p2.getVlrTexto() : "Sala";
			parametroZona = zona + "/" + sala;			
		} catch (ApplicationBusinessException e) {
			logError(e);
		}
		StringBuilder str = new StringBuilder();
		if (grade != null) {
			str.append("<B>Grade de consulta: </B>");
			str.append(grade.getSeq());
			str.append(", <B>" + parametroZona + ": </B>");
			if (grade.getSiglaUnfSala() != null) {
				str.append(grade.getSiglaUnfSala().getSigla());
				str.append(" - ");
				str.append(grade.getSiglaUnfSala().getId().getSala());
			}
			str.append(", <B>Esp/Agenda: </B>");
			if (grade.getEspecialidade() != null) {
				str.append(grade.getEspecialidade().getNomeReduzido());
			}
			str.append(", <B>Equipe: </B>");
			if (grade.getEquipe() != null) {
				str.append(grade.getEquipe().getNome());
			}
			if (grade.getProfEspecialidade() != null) {
				str.append(", <B>Profissional: </B>");
				str.append(grade.getProfEspecialidade().getRapServidor().getPessoaFisica().getNome());
			}
		}
		str.append('.');
		
		return str.toString();

	}
    
    public FatConvenioSaudePlano popularPagador(Integer numero) throws ApplicationBusinessException {
		FatConvenioSaudePlano retorno = null;
	
		if (numero != null) {
		    AacConsultas consulta = aacConsultasDAO.obterOriginal(numero);
		    Short pagadorSus = getParametroFacade().buscarValorShort(AghuParametrosEnum.P_AGHU_CONVENIO_SUS);
	
		    if (consulta.getFormaAgendamento() != null && consulta.getConvenioSaudePlano() == null && consulta.getPagador().getSeq().equals(pagadorSus)) {
			retorno = popularPagadorSus(consulta);
		    }
	
		    if (consulta.getFormaAgendamento() == null && consulta.getGradeAgendamenConsulta().getFormaAgendamento() != null) {
			consulta.setFormaAgendamento(consulta.getGradeAgendamenConsulta().getFormaAgendamento());
			retorno = popularProjeto(consulta);
		    }
		}
	
		return retorno;
    }

    private FatConvenioSaudePlano popularProjeto(AacConsultas consulta) throws ApplicationBusinessException {
		FatConvenioSaudePlano retorno = null;
		Integer projetoPesquisa = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_PGD_SEQ_PESQUISA);
	
		if (consulta.getFormaAgendamento() != null && consulta.getFormaAgendamento().getPagador().getSeq().equals(projetoPesquisa)) {
	
		    if (consulta.getGradeAgendamenConsulta().getProjetoPesquisa() == null
			    || consulta.getGradeAgendamenConsulta().getProjetoPesquisa().getConvenioSaudePlano() == null
			    || !consulta.getGradeAgendamenConsulta().getProjetoPesquisa().getConvenioSaudePlano().getIndTipoPlano()
				    .equals(DominioTipoPlano.A)) {
	
			if (consulta.getConvenioSaudePlano() == null) {
			    MarcacaoConsultaONExceptionCode.AAC_00778.throwException();
			}
	
		    } else if (consulta.getConvenioSaudePlano() == null) {
			retorno = consulta.getGradeAgendamenConsulta().getProjetoPesquisa().getConvenioSaudePlano();
		    }
		}
		if (consulta.getProjetoPesquisa() != null
			&& (consulta.getFormaAgendamento() == null || consulta.getFormaAgendamento().getPagador().getSeq().equals(projetoPesquisa))) {
		    MarcacaoConsultaONExceptionCode.AAC_00728.throwException();
		}
		return retorno;
    }

    private FatConvenioSaudePlano popularPagadorSus(AacConsultas consulta) throws ApplicationBusinessException {
		FatConvenioSaudePlano retorno = null;
		Byte tagHcpa = getParametroFacade().buscarValorByte(AghuParametrosEnum.P_TAG_HCPA);
		Byte tagUfrgs = getParametroFacade().buscarValorByte(AghuParametrosEnum.P_TAG_UFRGS);
		Byte planoHcpa = getParametroFacade().buscarValorByte(AghuParametrosEnum.P_PLANO_HCPA);
		Byte planoUfrgs = getParametroFacade().buscarValorByte(AghuParametrosEnum.P_PLANO_UFRGS);
		Byte planoAmbu = getParametroFacade().buscarValorByte(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
		Short convenioSusPadrao = getParametroFacade().buscarValorShort(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);
		Byte plano = null;
	
		if (consulta.getFormaAgendamento().getTipoAgendamento().getSeq().equals(tagHcpa)) {
		    plano = planoHcpa;
		} else if (consulta.getFormaAgendamento().getTipoAgendamento().getSeq().equals(tagUfrgs)) {
		    plano = planoUfrgs;
		} else {
		    plano = planoAmbu;
		}
	
		if (getVAacConvenioPlanoDAO().obterVAacConvenioPlanoAtivoPorId(convenioSusPadrao, plano) == null) {
		    MarcacaoConsultaONExceptionCode.AAC_00158.throwException();
		} else {
		    retorno = getFaturamentoFacade().obterConvenioSaudePlano(convenioSusPadrao, plano);
		}
		return retorno;
    }

    /*
     * ORADB:AACC_VER_CARACT_ESP
     */
    public Boolean verificarCaracEspecialidade(AghEspecialidades especialidade, DominioCaracEspecialidade caracteristica) {
		AghCaractEspecialidadesId id = new AghCaractEspecialidadesId(especialidade.getSeq(), caracteristica);
		return (getAghuFacade().obterCaracteristicaEspecialidadePorChavePrimaria(id) != null);
    }

    private AacConsultas validarBanco(AacConsultas consulta) throws ApplicationBusinessException {
		Integer pagadorSus = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_SUS).getVlrNumerico().intValue();
		Integer planoAmbu = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO).getVlrNumerico().intValue();
		consulta.setSituacaoConsulta(this.obterSituacaoMarcada());
	
		if (consulta.getFormaAgendamento() == null) {
			if (consulta.getGradeAgendamenConsulta() != null){
				consulta.setFormaAgendamento(consulta.getGradeAgendamenConsulta().getFormaAgendamento());
			}
		    if (consulta.getPagador() !=null && consulta.getPagador().getSeq() != null && consulta.getPagador().getSeq().equals(pagadorSus)) {
				FatConvenioSaudePlano convenioSaudePlano = getFaturamentoFacade().obterConvenioSaudePlano(pagadorSus.shortValue(),
					planoAmbu.byteValue());
				consulta.setConvenioSaudePlano(convenioSaudePlano);
		    }
		}
		return consulta;
    }

    private void validaProjeto(AacConsultas consulta) throws ApplicationBusinessException {
		AghParametros p1 = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PGD_SEQ_PESQUISA);
	
		if (consulta.getProjetoPesquisa() != null && !consulta.getProjetoPesquisa().getSeq().equals(p1.getVlrNumerico())) {
		    MarcacaoConsultaONExceptionCode.AAC_00728.throwException();
		}
    }

    /**
     * Popula a VO do Relatório de Agendamento.
     * 
     * @param consulta
     * @return
     * @throws ApplicationBusinessException
     */
    public RelatorioAgendamentoConsultaVO popularAgendamentoConsulta(AacConsultas consulta) throws ApplicationBusinessException {
    	
    	consulta = getAacConsultasDAO().merge(consulta);
    	RelatorioAgendamentoConsultaVO relatorioVO = new RelatorioAgendamentoConsultaVO();
		relatorioVO.setNumeroConsulta(consulta.getNumero());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		relatorioVO.setDataConsulta(sdf.format(consulta.getDtConsulta()));
		sdf = new SimpleDateFormat("HH:mm");
		relatorioVO.setHoraConsulta(sdf.format(consulta.getDtConsulta()));
		DominioDiaSemana diaSemana = CoreUtil.retornaDiaSemana(consulta.getDtConsulta());
		relatorioVO.setDiaConsulta(diaSemana.toString().substring(0, 3));
		relatorioVO.setDescricaoSala(consulta.getGradeAgendamenConsulta().getSiglaUnfSala().getDescricao());
		relatorioVO.setSala(consulta.getGradeAgendamenConsulta().getSiglaUnfSala().getId().getSala().toString());
		
		insereDadosUndFuncional(consulta, relatorioVO);
		
	
		if (consulta.getPagador() != null) {
		    StringBuffer informacoesConsulta = new StringBuffer(consulta.getPagador().getDescricao());
		    if (consulta.getTipoAgendamento() != null) {
			informacoesConsulta.append('/').append(consulta.getTipoAgendamento().getDescricao());
		    }
		    if (consulta.getCondicaoAtendimento() != null) {
			informacoesConsulta.append('/').append(consulta.getCondicaoAtendimento().getDescricao());
		    }
		    relatorioVO.setInformacoesConsulta(informacoesConsulta.toString());
		}
		relatorioVO.setNomeReduzidoEspecialidade(consulta.getGradeAgendamenConsulta().getEspecialidade().getNomeReduzido());
		relatorioVO.setNomeEquipe(consulta.getGradeAgendamenConsulta().getEquipe().getNome());
	
		if (consulta.getGradeAgendamenConsulta().getProfEspecialidade() != null// PARTE F01
			&& consulta.getGradeAgendamenConsulta().getProfEspecialidade().getRapServidor().getPessoaFisica().getNome() != null) {
		    relatorioVO.setProfissional(consulta.getGradeAgendamenConsulta().getProfEspecialidade().getRapServidor().getPessoaFisica().getNome());
		} else {
		    relatorioVO.setProfissional("EQUIPE");
		}
	
		populaDadosPaciente(consulta, relatorioVO);
		relatorioVO.setSeqGrade(consulta.getGradeAgendamenConsulta().getSeq());
	
		if (consulta.getServidorConsultado() != null) {
		    relatorioVO.setSerVinCodigoConsultado(consulta.getServidorConsultado().getId().getVinCodigo());
		    relatorioVO.setSerMatriculaConsultado(consulta.getServidorConsultado().getId().getMatricula());
		}
	
		if (consulta.getExcedeProgramacao()!=null && consulta.getExcedeProgramacao()) {
		    relatorioVO.setInfoExcedeProgramacao("Aguarde, sua Consulta é EXTRA.");
		}
		
		if (consulta.getServidorMarcacao() != null) {//PARTE F2
			if (StringUtils.isNotBlank(consulta.getServidorMarcacao().getPessoaFisica().getNome())){
				relatorioVO.setNomeUsuario(consulta.getServidorMarcacao().getPessoaFisica().getNome());
			}
			else{
				relatorioVO.setNomeUsuario("");	
			}
		}
		return relatorioVO;
    }

	private void insereDadosUndFuncional(AacConsultas consulta, RelatorioAgendamentoConsultaVO relatorioVO) {
		
		relatorioVO.setSiglaSala(consulta.getGradeAgendamenConsulta().getUnidadeFuncional().getDescricao());
		relatorioVO.setAndar(consulta.getGradeAgendamenConsulta().getSiglaUnfSala().getUnidadeFuncional().getAndar());
		
		if(consulta.getGradeAgendamenConsulta().getSiglaUnfSala().getUnidadeFuncional().getSetor() != null){
			relatorioVO.setSetor(consulta.getGradeAgendamenConsulta().getSiglaUnfSala().getUnidadeFuncional().getSetor());
		} else {
			relatorioVO.setSetor("");
		}
		
		if (consulta.getGradeAgendamenConsulta().getUnidadeFuncional().getIndAla() != null) {
			relatorioVO.setAlaBloco(consulta.getGradeAgendamenConsulta().getUnidadeFuncional().getIndAla().getDescricao());
		} else {
			relatorioVO.setAlaBloco("");
		}
	}

    private void populaDadosPaciente(AacConsultas consulta, RelatorioAgendamentoConsultaVO relatorioVO) {
		
		if (consulta.getPaciente().getCodigo() != null) {
		    relatorioVO.setCodigoPaciente(consulta.getPaciente().getCodigo());
		}
		if (consulta.getPaciente().getProntuario() != null) {
		    relatorioVO.setProntuario(consulta.getPaciente().getProntuario());
		}
		relatorioVO.setNomePaciente(consulta.getPaciente().getNome());
	
		AipGrupoFamiliarPacientes grupoFamiliar = ambulatorioFacade.obterProntuarioFamiliaPaciente(consulta.getPaciente().getCodigo());
	    if(grupoFamiliar!=null){
	    	 relatorioVO.setProntFamilia(grupoFamiliar.getAgfSeq());
	    }
		if (consulta.getPaciente().getNomeSocial() != null) {
		    relatorioVO.setNomeSocial(consulta.getPaciente().getNomeSocial());
		}
    }

    public List<RelatorioAgendamentoConsultaVO> obterAgendamentoConsulta(Integer nroConsulta) {
		List<RelatorioAgendamentoConsultaVO> relatorio = new ArrayList<RelatorioAgendamentoConsultaVO>();
		List<AacConsultas> consultas = getAacConsultasDAO().obterAgendamentoConsulta(nroConsulta);
		for (AacConsultas consulta : consultas) {
		    try {
			relatorio.add(popularAgendamentoConsulta(consulta));
		    } catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		    }
		}
		return relatorio;
    }

    /**
     * Marca Falta para todos os pacientes que estão agendados e não chegaram e
     * os que estão aguardando.
     * 
     * @param pacientesAgendados
     *            , pacientesAguardando
     * 
     * @throws BaseException
     * @throws NumberFormatException
     */
    public void marcarFaltaPacientes(Date dtPesquisa, List<VAacSiglaUnfSala> zonaSalas, VAacSiglaUnfSala zonaSala,
	    DataInicioFimVO dataInicioFim, AghEquipes equipe, EspCrmVO espCrmVO, AghEspecialidades especialidade,
	    RapServidores profissional, String nomeMicrocomputador) throws NumberFormatException, BaseException {

    	AacRetornos retorno = recuperaSituacaoAtendimentoPacienteFaltou();
    	List<AacConsultas> listaPacientesConsultaAgendada = this.pesquisarPacientesConsultasAgendadas(dtPesquisa, zonaSalas, zonaSala, dataInicioFim, equipe, espCrmVO, especialidade, profissional);
		for (AacConsultas consulta : listaPacientesConsultaAgendada) {
		    atualizarRetornoConsulta(consulta.getNumero(), retorno, nomeMicrocomputador, new Date());
		}
    }
    /**
     * Marca Falta para o paciente que foi selecionado e não chegou
     * 
     * @param consultaNumero
     *            , nomeMicrocomputador
     * 
     * @throws BaseException
     * @throws NumberFormatException
     */
    public void marcarFaltaPaciente(Integer consultaNumero, String nomeMicrocomputador, boolean chegou,Integer codSituacaoAtend) throws NumberFormatException, BaseException {
            if(chegou){
	    		MarcacaoConsultaONExceptionCode.ERRO_MARCAR_FALTA.throwException();
            }
        	AacRetornos retorno = recuperaSituacaoAtendimentoPacienteFaltou(codSituacaoAtend);
        	List<AacConsultas> listaPacientesConsultaAgendada = this.getAacConsultasDAO().pesquisaConsultasNumero(consultaNumero);
    		for (AacConsultas consulta : listaPacientesConsultaAgendada) {
    		    atualizarRetornoConsulta(consulta.getNumero(), retorno, nomeMicrocomputador, new Date());
    		}
        }
    /**
     * Altera o retorno da consulta.
     * 
     * @param consulta
     *            , retorno
     * @throws NumberFormatException
     * @throws BaseException
     */
    public void atualizarRetornoConsulta(Integer numeroConsulta, AacRetornos retorno, String nomeMicrocomputador,
	    final Date dataFimVinculoServidor) throws NumberFormatException, BaseException {
		final AacConsultas consulta = getAacConsultasDAO().obterPorChavePrimaria(numeroConsulta);
		AacRetornos retornoBD = recuperaSituacaoAtendimentoPacienteFaltou();
		
		if (consulta.getControle() != null && consulta.getControle().getSituacaoAtendimento() != null && consulta.getControle().getSituacaoAtendimento().getAtendConcluido() && retorno.equals(retornoBD)) {
		    MarcacaoConsultaONExceptionCode.AAC_00612.throwException();
		} else {
		    AacConsultas consultaAnterior = getAmbulatorioFacade().clonarConsulta(consulta);
		    consulta.setRetorno(retorno);
		    getAmbulatorioConsultaRN().atualizarConsulta(consultaAnterior, consulta, false, nomeMicrocomputador, new Date(), false, true);
		    // Interface faturamento: atualiza faturamento dos procedimentos de
		    // acordo
		    // com o atributo faturaSus do retorno informado
		    List<AacConsultaProcedHospitalar> listaConsultaProcedHospitalar = getAacConsultaProcedHospitalarDAO().buscarConsultaProcedHospPorNumeroConsulta(consulta.getNumero());
		    if (!listaConsultaProcedHospitalar.isEmpty()) {
			for (AacConsultaProcedHospitalar consultaProcedHospitalar : listaConsultaProcedHospitalar) {
			    getFaturamentoFacade().atualizarFaturamentoProcedimentoConsulta(consultaProcedHospitalar.getConsultas().getNumero(), consultaProcedHospitalar.getProcedHospInterno().getSeq(),
				    consultaProcedHospitalar.getQuantidade().shortValue(), consultaProcedHospitalar.getConsultas().getRetorno(), nomeMicrocomputador, dataFimVinculoServidor);
			}
		    }
		}
    }
    
    private AacRetornos recuperaSituacaoAtendimentoPacienteFaltou() {
		return getAacRetornosDAO().obterPorChavePrimaria(DominioSituacaoAtendimento.PACIENTE_FALTOU.getCodigo());
	}
    private AacRetornos recuperaSituacaoAtendimentoPacienteFaltou(Integer codSituacaoAtend) {
		return getAacRetornosDAO().obterPorChavePrimaria(codSituacaoAtend);
	}
    public List<AacConsultas> pesquisarPacientesConsultasAgendadas(Date dtPesquisa, List<VAacSiglaUnfSala> zonaSalas,
	    VAacSiglaUnfSala zonaSala, DataInicioFimVO turno, AghEquipes equipe, EspCrmVO espCrmVO, AghEspecialidades especialidade,RapServidores profissional) throws ApplicationBusinessException {
    	AacRetornos retorno = getAacRetornosDAO().obterPorChavePrimaria(DominioSituacaoAtendimento.PACIENTE_AGENDADO.getCodigo());
    	
		return getAacConsultasDAO().pesquisarPacientesConsultasAgendadas(dtPesquisa, zonaSalas, zonaSala, turno, equipe, espCrmVO, especialidade, profissional, retorno);
    }

    /**
     * Retorna consultar para impressoras Matriciais - Relatório de Agendamento.
     * 
     * @param consulta
     * @return
     * @throws ApplicationBusinessException
     */
    @SuppressWarnings("PMD.NPathComplexity")
    public String obterTextoAgendamentoConsulta(String hospitalLocal, String unidadeFuncional, String sala, AacConsultas consulta, boolean flag)
	    throws ApplicationBusinessException {
		SimpleDateFormat dataConsulta = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat horaConsulta = new SimpleDateFormat("HH:mm");
		SimpleDateFormat horaConsultaEmissao = new SimpleDateFormat("HH:mm:ss");
		DominioDiaSemana diaSemana = CoreUtil.retornaDiaSemana(consulta.getDtConsulta());
	
		StringBuilder str = new StringBuilder(392);
		consulta = aacConsultasDAO.obterPorChavePrimaria(consulta.getNumero());
		String valorParametroNegrito = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CUPS_IMP_BEMATECH_NEGRITO).getVlrTexto();
		String valorParametroCabecalhoCupomAtendimento = getParametroFacade().buscarAghParametro(
			AghuParametrosEnum.P_AMB_CABECALHO_CUPOM_ATENDIMENTO).getVlrTexto();
		String tituloUBS = null;
		if (aghuFacade.possuiCaracteristicaPorUnidadeEConstante(consulta.getGradeAgendamenConsulta().getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UBS)) {
		    try {
			tituloUBS = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_TITULO_UBS);
			tituloUBS = WordUtils.capitalizeFully(tituloUBS).replaceAll(" Da ", " da ").replaceAll(" De ", " de ").replaceAll(" Do ", " do ");
		    } catch (Exception e) {
			LOG.error(e.getMessage(), e);
		    }
	
		}
	
		if ("S".equalsIgnoreCase(valorParametroNegrito)) {
		    str.append((char) 27).append((char) 69);// INICIA NEGRITO
		}
	
		str.append("----------------------------------------\n");
	
		if (tituloUBS != null && !"".equals(tituloUBS)) {
		    String sNovaLinhas = tituloUBS.replaceAll("n_l", "\n").toString();
		    str.append(sNovaLinhas).append('\n');
		} else if (valorParametroCabecalhoCupomAtendimento != null && !"".equals(valorParametroCabecalhoCupomAtendimento)) {
		    // Cabecalho dinamico
		    String sNovaLinhas = valorParametroCabecalhoCupomAtendimento.replaceAll("n_l", "\n").toString();
		    str.append(sNovaLinhas).append('\n');
		} else {
		    // Cabecalho default
		    str.append(hospitalLocal).append('\n');
		}
	
		str.append("----------------------------------------\n\nData/Hora ").append(dataConsulta.format(consulta.getDtConsulta()))
			.append(' ').append(diaSemana.toString().substring(0, 3)).append(' ').append(horaConsulta.format(consulta.getDtConsulta())).append("\n\n")
			.append(unidadeFuncional).append("  ").append(consulta.getGradeAgendamenConsulta().getSiglaUnfSala().getSigla()).append("         ").append(sala).append("  ")
			.append(consulta.getGradeAgendamenConsulta().getSiglaUnfSala().getId().getSala().toString()).append('\n');
		if (consulta.getCondicaoAtendimento() != null) {
		    str.append("Consulta ").append(consulta.getCondicaoAtendimento().getDescricao()).append('\n');
		}
		if (consulta.getPagador() != null) {
		    str.append("Pagador ").append(consulta.getPagador().getDescricao()).append('\n');
		}
		if (consulta.getTipoAgendamento() != null) {
		    str.append("Autorizacao ").append(consulta.getTipoAgendamento().getDescricao()).append('\n');
		}
		str.append("Para ").append(consulta.getGradeAgendamenConsulta().getEspecialidade().getNomeEspecialidade()).append("\n\nEquipe ")
			.append(consulta.getGradeAgendamenConsulta().getEquipe().getNome()).append('\n');
		if (consulta.getGradeAgendamenConsulta().getProfEspecialidade() != null
			&& consulta.getGradeAgendamenConsulta().getProfEspecialidade().getRapServidor().getPessoaFisica().getNome() != null) {
		    str.append("Profissional ")
			    .append(consulta.getGradeAgendamenConsulta().getProfEspecialidade().getRapServidor().getPessoaFisica().getNome())
			    .append('\n');
		} else {
		    str.append("Profissional EQUIPE\n");
		}
		if (consulta.getPaciente().getProntuario() != null) {
		    str.append("Prontuario ").append(consulta.getPaciente().getProntuario());
		    AipGrupoFamiliarPacientes grupoFamiliar = ambulatorioFacade.obterProntuarioFamiliaPaciente(consulta.getPaciente().getCodigo());
		    if(grupoFamiliar!=null){
		    	str.append("   Pront. Familia ").append(grupoFamiliar.getAgfSeq()).append('\n');
		    }
		    else {
				str.append('\n');
		    }
		}
		if (consulta.getPaciente().getCodigo() != null) {
		    str.append("Paciente ").append(consulta.getPaciente().getCodigo()).append('\n');
		}
		str.append(' ').append(consulta.getPaciente().getNome()).append('\n');
	
		if (StringUtils.isNotBlank(consulta.getPaciente().getNomeSocial())) {
		    str.append(" (").append(consulta.getPaciente().getNomeSocial()).append(")\n\n");
		} else {
		    str.append('\n');
		}
		str.append("Grade ").append(consulta.getGradeAgendamenConsulta().getSeq()).append('\n');
		if (consulta.getServidorConsultado() != null) {
		    str.append("Matricula ").append(consulta.getServidorConsultado().getId().getMatricula());
		}
		str.append("\n\n");
	
		if (consulta.getGradeAgendamenConsulta().getEspecialidade().getSigla().equals("AUD")) {
		    str.append("Fazer repouso acústico, por um período mínimo, de 14 horas, conforme NR 7, item 3.6.1.2.\n");
		}
		if (consulta.getExcedeProgramacao() != null && consulta.getExcedeProgramacao()) {
		    str.append("Aguarde, sua Consulta EXTRA.\n");
		}
		if(!flag){
			str.append("Marcado por: ");
			if(consulta.getServidorMarcacao()!=null) {
				if (StringUtils.isNotBlank(consulta.getServidorMarcacao().getPessoaFisica().getNome())){
					str.append(consulta.getServidorMarcacao().getPessoaFisica().getNome());				
				}
			}
		}		
		else{//flag incluido para atender #8253
			str.append("Marcado por: ").append(this.obterNomeServidorMarcado(consulta.getNumero(), consulta.getExcedeProgramacao(),consulta));
		}
		String valorParametroGuilhotina = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CUPS_IMP_BEMATECH_GUILHOTINA).getVlrTexto();
		str.append("\nNro Consulta: ").append(consulta.getNumero()).append('\n');
		Date dataEmissao = new Date();
		str.append("\nData Emissão: ").append(dataConsulta.format(dataEmissao)).append(' ').append(horaConsultaEmissao.format(dataEmissao)).append("\n\n");
	
		if ("S".equalsIgnoreCase(valorParametroGuilhotina)) {
		    str.append("\n\n\n\n").append((char) 27).append((char) 119);
		}
	
		if ("S".equalsIgnoreCase(valorParametroNegrito)) {
		    str.append((char) 27).append((char) 70);// FIM NEGRITO
		}
	
		return str.toString();
    }

    public String obterTextoAgendamentoConsulta(String hospitalLocal, String unidadeFuncional, String sala, AacConsultas consulta) throws ApplicationBusinessException{
    	return this.obterTextoAgendamentoConsulta(hospitalLocal, unidadeFuncional, sala, consulta, false);
    }

    public List<AacConsultas> listarConsultasNaoRealizadasPorGrade(AacGradeAgendamenConsultas grade, Short pgdSeq, Short tagSeq,
	    Short caaSeq, Boolean emergencia, Date dtConsulta, Date mesInicio, Date mesFim, Date horaConsulta, DominioDiaSemana diaSemana,
	    boolean excluirPrimeiraConsulta, boolean visualizarPrimeirasConsultasSMS)
	    throws ApplicationBusinessException {

		String valorParametro = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_PERMITE_CONSULTA_HORARIOS_AMBULATORIO_PASSADOS);
		boolean mostrarTodosHorarios = false;
		if (valorParametro.equalsIgnoreCase("S")) {
		    mostrarTodosHorarios = true;
		}
		return getAacConsultasDAO().listarConsultasNaoRealizadasPorGrade(grade, pgdSeq, tagSeq, caaSeq, emergencia, dtConsulta, mesInicio,
			mesFim, horaConsulta, diaSemana, mostrarTodosHorarios, excluirPrimeiraConsulta, visualizarPrimeirasConsultasSMS);
    }

    public RelatorioAgendamentoConsultaVO popularTicketAgendamentoConsulta(AacConsultas consulta) throws ApplicationBusinessException {
    	RelatorioAgendamentoConsultaVO relatorioVO = popularAgendamentoConsulta(consulta);
    	
    	//Parte F3
    	if(consulta.getServidorConsultado() != null && consulta.getServidorConsultado().getCentroCustoLotacao()!=null){
			relatorioVO.setDescricaoCentroCusto(consulta.getServidorConsultado().getCentroCustoLotacao().getDescricao());
		}
    	
    	//Parte F2
    	relatorioVO.setNomeUsuario(obterNomeServidorMarcado(consulta.getNumero(), consulta.getExcedeProgramacao(),consulta));
    	return relatorioVO;
    }
    /**
     * 
     * @param consultaNumero
     * @param servidor
     * @return
     */
    public String obterNomeServidorMarcado(Integer consultaNumero,Boolean pIndExcede,AacConsultas consulta){
    	
    	if(pIndExcede!=null && pIndExcede.equals(Boolean.FALSE)){
    		List<AacConsultasJn> AacConsultasJn = ambulatorioFacade.pesquisarUsuariosMarcadorConsulta(consultaNumero);
    		if(AacConsultasJn!=null && !AacConsultasJn.isEmpty() && AacConsultasJn.get(0).getJnUsuario()!=null){
    				return AacConsultasJn.get(0).getJnUsuario();
    		}
    	}
    	else{
    		if(consulta.getServidor()!=null && consulta.getServidor().getCseUsuario()!= null && consulta.getServidor().getCseUsuario().iterator().hasNext()){
    			return consulta.getServidor().getCseUsuario().iterator().next().getId();
    		}
    	}
    	return "";
    }

    protected AacConsultasDAO getAacConsultasDAO() {
	return aacConsultasDAO;
    }

    protected VAacConvenioPlanoDAO getVAacConvenioPlanoDAO() {
	return vAacConvenioPlanoDAO;
    }

    protected IPacienteFacade getPacienteFacade() {
	return pacienteFacade;
    }

    protected IAghuFacade getAghuFacade() {
	return aghuFacade;
    }

    protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
	return pesquisaInternacaoFacade;
    }

    protected IParametroFacade getParametroFacade() {
	return parametroFacade;
    }

    protected AacRetornosDAO getAacRetornosDAO() {
	return aacRetornosDAO;
    }

    protected AacSituacaoConsultasDAO getAacSituacaoConsultasDAO() {
	return aacSituacaoConsultasDAO;
    }

    protected AacHorarioGradeConsultaDAO getAacHorarioGradeConsultaDAO(){
		return aacHorarioGradeConsultaDAO;
	}
    
    protected IAmbulatorioFacade getAmbulatorioFacade() {
	return this.ambulatorioFacade;
    }

    protected AmbulatorioConsultaRN getAmbulatorioConsultaRN() {
	return ambulatorioConsultaRN;
    }

    protected IFaturamentoFacade getFaturamentoFacade() {
	return faturamentoFacade;
    }

    protected AacConsultaProcedHospitalarDAO getAacConsultaProcedHospitalarDAO() {
	return aacConsultaProcedHospitalarDAO;
    }

    public IServidorLogadoFacade getServidorLogadoFacade() {
	return servidorLogadoFacade;
    }

    public PesquisarPacientesAgendadosON getPesquisarPacientesAgendadosON() {
	return pesquisarPacientesAgendadosON;
    }

	public AacGradeAgendamenConsultasDAO getGradeAgendamentoConsultasDAO() {
		return gradeAgendamentoConsultasDAO;
}
}
