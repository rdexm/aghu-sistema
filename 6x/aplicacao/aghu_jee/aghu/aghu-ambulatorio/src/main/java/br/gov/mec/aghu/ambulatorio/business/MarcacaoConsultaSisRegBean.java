package br.gov.mec.aghu.ambulatorio.business;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasSisregDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacSituacaoConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacTipoConsultaSisregDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.fonetizador.FonetizadorUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioOrigemConsulta;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoEndereco;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacConsultasSisreg;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AacTipoConsultaSisreg;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipEnderecosPacientesId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class MarcacaoConsultaSisRegBean extends BaseBusiness {
	
	private static final long serialVersionUID = 7733221181103157778L;
		
	private static final Log LOG = LogFactory.getLog(MarcacaoConsultaSisRegBean.class);
	
	public enum MarcacaoConsultaSisRegBeanExceptionCode implements BusinessExceptionCode {
		ERRO_MARCACAO_CONSULTA_SISREG_CONSULTA_CORRESPONDENTE, ERRO_MARCACAO_CONSULTA_SISREG_CONVENIO_DEFAULT,
		ERRO_MARCACAO_CONSULTA_SISREG_INSERIR_PACIENTE, ERRO_MARCACAO_CONSULTA_SISREG_PESQUISAR_PACIENTE_DADOS_DUPLICIDADE,
		ERRO_MARCACAO_CONSULTA_SISREG_PESQUISAR_PACIENTE_CNS_DUPLICIDADE, ERRO_MARCACAO_CONSULTA_SISREG_SERVIDOR_NAO_ENCONTRADO;
	}
	
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private MarcacaoConsultaON marcacaoConsultaON;
	
	
	@Inject
	private AacTipoConsultaSisregDAO aacTipoConsultaSisregDAO;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private AacSituacaoConsultasDAO aacSituacaoConsultasDAO;
	
	@Inject
	private AacConsultasSisregDAO aacConsultasSisregDAO;
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void processarRegistroSisreg(AacConsultasSisreg consultaSisreg, String nomeMicrocomputador) throws ApplicationBusinessException {
		try {
			AipPacientes paciente;
			paciente = this.pesquisarPacientePorCns(consultaSisreg.getCnsPaciente());
			if (paciente == null) {
				paciente = this.pesquisarPacientePorNomeDtNacimentoNomeMae(consultaSisreg.getNomePaciente(),
						consultaSisreg.getDtNasc(), consultaSisreg.getMaePaciente());				
				if (paciente == null) {
					paciente = this.inserirPacienteEEnderecoSemValidacao(consultaSisreg);
				}
			}

			AacConsultas consulta = pesquisarHorarioConsulta(consultaSisreg);
			if (consulta == null) {
				throw new ApplicationBusinessException(
						MarcacaoConsultaSisRegBeanExceptionCode.ERRO_MARCACAO_CONSULTA_SISREG_CONSULTA_CORRESPONDENTE);
			}
			
			AacSituacaoConsultas situacaoMarcada = getAacSituacaoConsultasDAO().obterSituacaoConsultaPeloId("M");

			AacConsultas consultaAnterior = getAmbulatorioFacade().clonarConsulta(consulta);
			consulta.setPaciente(paciente);
			consulta.setExcedeProgramacao(Boolean.FALSE);
			consulta.setOrigem(DominioOrigemConsulta.S);
			consulta.setSituacaoConsulta(situacaoMarcada);
			consulta.setDthrMarcacao(new Date());
			consulta.setCodCentralSol(consultaSisreg.getSeq());
			FatConvenioSaudePlano convenioPlano = obterConvenioAmbulatorio();
			if (convenioPlano == null) {
				throw new ApplicationBusinessException(
						MarcacaoConsultaSisRegBeanExceptionCode.ERRO_MARCACAO_CONSULTA_SISREG_CONVENIO_DEFAULT);
			} else {
				consulta.setConvenioSaudePlano(convenioPlano);
			}
			
			getMarcacaoConsultaON().manterConsulta(consultaAnterior, consulta, nomeMicrocomputador, false);

			consultaSisreg.setMarcado(true);
			getAacConsultasSisregDAO().atualizar(consultaSisreg);
			getAacConsultasSisregDAO().flush();

		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(e);
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			throw e;
		} 
	}
	
	private AipPacientes inserirPacienteEEnderecoSemValidacao(AacConsultasSisreg consultaSisreg) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
		
		AipPacientes paciente = new AipPacientes();
		try {
			paciente.setNome(MarcacaoConsultaSisregUtil.formataString(FonetizadorUtil.ajustarNome(consultaSisreg.getNomePaciente()), 50));
			paciente.setNomeMae(MarcacaoConsultaSisregUtil.formataString(FonetizadorUtil.ajustarNome(consultaSisreg.getMaePaciente()), 50));
			paciente.setDtNascimento(consultaSisreg.getDtNasc());
			paciente.setDtIdentificacao(new Date());
			paciente.setGerarProntuario(false);
			paciente.setCadConfirmado(DominioSimNao.N);
			paciente.setIndPacienteVip(DominioSimNao.N);
			paciente.setRapServidoresCadastro(servidorLogado);
			paciente.setFccCentroCustosCadastro(servidorLogado.getCentroCustoLotacao());
			paciente.setCriadoEm(new Date());
			
			getCadastroPacienteFacade().persistirPacienteFonemas(paciente);
			
			AipEnderecosPacientesId idEndereco = new AipEnderecosPacientesId(paciente.getCodigo(), Short.valueOf("1"));

			AipEnderecosPacientes endereco = new AipEnderecosPacientes();
			endereco.setId(idEndereco);
			endereco.setTipoEndereco(DominioTipoEndereco.R);
			endereco.setIndPadrao(DominioSimNao.S);
			endereco.setLogradouro(MarcacaoConsultaSisregUtil.formataString(consultaSisreg.getLogradouro(), 60));
			if(!StringUtils.isBlank(consultaSisreg.getNroLogradouro()) && StringUtils.isNumeric(consultaSisreg.getNroLogradouro())){
				endereco.setNroLogradouro(Integer.valueOf(consultaSisreg.getNroLogradouro()));	
			}
			endereco.setComplLogradouro(MarcacaoConsultaSisregUtil.formataString(consultaSisreg.getComplLogradouro(), 20));
			endereco.setBairro(MarcacaoConsultaSisregUtil.formataString(consultaSisreg.getBairro(), 20));
			endereco.setCidade(MarcacaoConsultaSisregUtil.formataString(consultaSisreg.getNomeCidadePac(),25));
			endereco.setCep(consultaSisreg.getCep());

			getCadastroPacienteFacade().verificarCamposInclusaoEndereco(null,endereco.getLogradouro(), endereco.getCidade(), null,endereco.getId().getPacCodigo());
			endereco.setAipPaciente(paciente);
			paciente.getEnderecos().add(endereco);
			getCadastroPacienteFacade().atribuirSequencialEnderecos(paciente);
			getCadastroPacienteFacade().persistirPacienteSemValidacao(paciente);

		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					MarcacaoConsultaSisRegBeanExceptionCode.ERRO_MARCACAO_CONSULTA_SISREG_INSERIR_PACIENTE);
		}
		return paciente;
	}
	
	
	
	private AipPacientes pesquisarPacientePorNomeDtNacimentoNomeMae(String nome, Date dataNacimento, String nomeMae)
			throws ApplicationBusinessException {
		AipPacientes paciente = null;
		
		List<AipPacientes> listaPac = getPacienteFacade()
				.pesquisarPacientePorNomeDtNacimentoNomeMae(nome, dataNacimento, nomeMae);
		
		if (listaPac.size() > 1) {
			throw new ApplicationBusinessException(
					MarcacaoConsultaSisRegBeanExceptionCode.ERRO_MARCACAO_CONSULTA_SISREG_PESQUISAR_PACIENTE_DADOS_DUPLICIDADE);
		} else if (listaPac.size() == 1) {
			paciente = listaPac.get(0);
		}
		
		return paciente;
	}
	
	private AipPacientes pesquisarPacientePorCns(BigInteger numCartaoSaude)
			throws ApplicationBusinessException {
		AipPacientes paciente = null;

		List<AipPacientes> listaPac = getPacienteFacade()
				.pesquisarPacientePorNumeroCartaoSaude(numCartaoSaude);

		if (listaPac.size() > 1) {
			throw new ApplicationBusinessException(
					MarcacaoConsultaSisRegBeanExceptionCode.ERRO_MARCACAO_CONSULTA_SISREG_PESQUISAR_PACIENTE_CNS_DUPLICIDADE);
		} else if (listaPac.size() == 1) {
			paciente = listaPac.get(0);
		}

		return paciente;
	}
	
	private AacConsultas pesquisarHorarioConsulta(AacConsultasSisreg consultaSisreg) throws ApplicationBusinessException {
		
		RapServidores servidor = getRegistroColaboradorFacade().obterServidorAtivoPorCpf(consultaSisreg.getCpfMedicoSolicitante());
		
		if (servidor==null){
			throw new ApplicationBusinessException(
					MarcacaoConsultaSisRegBeanExceptionCode.ERRO_MARCACAO_CONSULTA_SISREG_SERVIDOR_NAO_ENCONTRADO);
		}
		
		AacConsultas consulta = null;
		String tipoConsulta = "U";
		AghParametros parametro;
		try {
			parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SIT_CONS_SISREG);
			tipoConsulta = parametro.getVlrTexto();
		} catch (ApplicationBusinessException e) {
			tipoConsulta = "U";
		}
		
		AacTipoConsultaSisreg tipoConsultaSisreg = getAacTipoConsultaSisregDAO()
				.obterTipoConsultaSisregPorSeq(consultaSisreg.getTipoConsulta());

		if (tipoConsultaSisreg != null && servidor !=null) {
			consulta = getAacConsultasDAO().obterConsultaPorCpfGrade(
					servidor, tipoConsultaSisreg.getFormaAgendamento(),
					consultaSisreg.getDtConsulta(), tipoConsulta);
		}
		return consulta;
	}
	
	private FatConvenioSaudePlano obterConvenioAmbulatorio() throws ApplicationBusinessException{	
		Short convenioPadrao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_CONVENIO_PADRAO_AMB_V1).getVlrNumerico().shortValue();
		Byte planoPadrao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_PLANO_PADRAO_AMB_V1).getVlrNumerico().byteValue();
		
		return this.getFaturamentoFacade().obterConvenioSaudePlano(
				convenioPadrao, planoPadrao);
	}
	
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AacTipoConsultaSisregDAO getAacTipoConsultaSisregDAO() {
		return aacTipoConsultaSisregDAO;
	}
	
	protected AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}
	
	protected AacSituacaoConsultasDAO getAacSituacaoConsultasDAO(){
		return aacSituacaoConsultasDAO;
	}
	
	protected AacConsultasSisregDAO getAacConsultasSisregDAO() {
		return aacConsultasSisregDAO;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected MarcacaoConsultaON getMarcacaoConsultaON() {
		return marcacaoConsultaON;
	}


	@Override
	protected Log getLogger() {
		return LOG;
	}


}
