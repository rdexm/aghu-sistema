package br.gov.mec.aghu.blococirurgico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.dao.MamLaudoAihDAO;
import br.gov.mec.aghu.blococirurgico.dao.VAinServInternaDAO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioLaudoAIHSolicVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioLaudoAIHVO;
import br.gov.mec.aghu.blococirurgico.vo.VFatSsmInternacaoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;

@Stateless
public class RelatorioLaudoAIHON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioLaudoAIHON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private VAinServInternaDAO vAinServInternaDAO;


	@EJB
	private IPrescricaoMedicaFacade iPrescricaoMedicaFacade;

	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;

	@EJB
	private IPacienteFacade iPacienteFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private ICadastroPacienteFacade iCadastroPacienteFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade iCadastrosBasicosInternacaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private MamLaudoAihDAO mamLaudoAihDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6202705725897677514L;

	public RelatorioLaudoAIHVO pesquisarLaudoAIH(
			Integer seqAtendimento,
			Integer codigoPac, Long seq, Integer matricula, 
			Short vinCodigo) 
	throws ApplicationBusinessException, ApplicationBusinessException {

		RelatorioLaudoAIHVO relatorioLaudoAIHVO = null;
		Enum[] fetchArgsLeftJoin = { MamLaudoAih.Fields.SERVIDOR_RESP_INTERNACAO,MamLaudoAih.Fields.PACIENTE };
		MamLaudoAih laudoAIH = mamLaudoAihDAO.obterPorChavePrimaria(seq,null, fetchArgsLeftJoin); 
		
		if (laudoAIH != null) {
			relatorioLaudoAIHVO = new RelatorioLaudoAIHVO();

			relatorioLaudoAIHVO.setMamLaudoAih(laudoAIH);
			
			relatorioLaudoAIHVO.setNomeHospital(getCadastrosBasicosInternacaoFacade()
					.recuperarNomeInstituicaoHospitalarLocal());
			relatorioLaudoAIHVO.setCnes(getAghuFacade()
					.recuperarCnesInstituicaoHospitalarLocal());

			relatorioLaudoAIHVO.setDtProvavelInternacao(laudoAIH.getDtProvavelInternacao()==null? laudoAIH.getDthrCriacao() : laudoAIH.getDtProvavelInternacao());
			relatorioLaudoAIHVO.setDtProvavelCirurgia(laudoAIH.getDtProvavelCirurgia());
			
			if(laudoAIH.getServidorRespInternacao()!=null){
				List<String> servInterna = getVAinServInternaDAO().obterVAinServInternaPorId(laudoAIH.getServidorRespInternacao().getId().getMatricula(), laudoAIH.getServidorRespInternacao().getId().getVinCodigo());
				if (servInterna.size()> 0){ 
					relatorioLaudoAIHVO.setEquipe(servInterna.get(0));
				}
			}

			processarDadosPaciente(laudoAIH.getPaciente().getCodigo(), relatorioLaudoAIHVO);

			processarEnderecoPaciente(relatorioLaudoAIHVO, laudoAIH);
		}

		processarInternacaoPaciente(seq, relatorioLaudoAIHVO);

		processarConselhoProfissional(laudoAIH.getServidorValida(), relatorioLaudoAIHVO);
		
		if (seqAtendimento != null){
			relatorioLaudoAIHVO.setNroConsulta("BA: " + StringUtil.adicionaZerosAEsquerda(seqAtendimento, 8));
		}
		
		return relatorioLaudoAIHVO;
	}

	private void processarInternacaoPaciente(Long seq,
			RelatorioLaudoAIHVO relatorioLaudoAIHVO)
			throws ApplicationBusinessException {
		AghParametros param = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		
		List<VFatSsmInternacaoVO> internacoesPaciente = getAmbulatorioFacade().pesquisarInternacoesPacienteByLaudo(seq, param.getVlrNumerico().shortValue());
		VFatSsmInternacaoVO internacaoPaciente = internacoesPaciente.get(0);

		relatorioLaudoAIHVO.setSinaisSintomas(internacaoPaciente.getSinaisSintomas());
		relatorioLaudoAIHVO.setCondicoes(internacaoPaciente.getCondicoes());
		relatorioLaudoAIHVO.setResultadosProvas(internacaoPaciente.getResultadosProvas());
		relatorioLaudoAIHVO.setCidCodigo(internacaoPaciente.getCidCodigo());
		relatorioLaudoAIHVO.setCidDescricao(internacaoPaciente.getCidDescricao());
		relatorioLaudoAIHVO.setCidCodigoSec(internacaoPaciente.getCidCodigoSec());
		
		if(internacaoPaciente.getDescricaoItemProcedimento() == null){
			relatorioLaudoAIHVO.setDescricaoProcedimento(internacaoPaciente.getDescricaoProcedimento());
		}else{
			relatorioLaudoAIHVO.setDescricaoProcedimento(internacaoPaciente.getDescricaoItemProcedimento());
		}
		
		if(internacaoPaciente.getPrioridade() == null){
			relatorioLaudoAIHVO.setPrioridade("");
		}else if(internacaoPaciente.getPrioridade() == 1){
			relatorioLaudoAIHVO.setPrioridade("ELETIVA");
		}else if (internacaoPaciente.getPrioridade() == 2){
			relatorioLaudoAIHVO.setPrioridade("EMERGÊNCIA");
		}else if (internacaoPaciente.getPrioridade() == 3){
			relatorioLaudoAIHVO.setPrioridade("URGÊNCIA");
		}
		
		relatorioLaudoAIHVO.setCodTabela(internacaoPaciente.getCodTabela());
		relatorioLaudoAIHVO.setHora(internacaoPaciente.getDthrCriacao());
	}

	private void processarEnderecoPaciente(
			RelatorioLaudoAIHVO relatorioLaudoAIHVO, MamLaudoAih laudoAIH) {

		VAipEnderecoPaciente enderecoPaciente = getCadastroPacienteFacade()
		.obterEndecoPaciente(laudoAIH.getPaciente().getCodigo());
		
		if (enderecoPaciente != null) {
			relatorioLaudoAIHVO.setEndereco(enderecoPaciente.getLogradouro() + (enderecoPaciente.getNroLogradouro() != null ? " " + enderecoPaciente.getNroLogradouro(): "")
							             + (enderecoPaciente.getComplLogradouro() != null ? '/' + enderecoPaciente.getComplLogradouro() : "") + " - " + enderecoPaciente.getBairro());
			relatorioLaudoAIHVO.setMunicipio(enderecoPaciente.getCidade());
			relatorioLaudoAIHVO.setCodigoIbge(enderecoPaciente.getCodIbge());
			relatorioLaudoAIHVO.setUf(enderecoPaciente.getUf());
			relatorioLaudoAIHVO.setCep(enderecoPaciente.getCep());
		} else {
			relatorioLaudoAIHVO.setEndereco(null);
			relatorioLaudoAIHVO.setMunicipio(null);
			relatorioLaudoAIHVO.setCodigoIbge(null);
			relatorioLaudoAIHVO.setUf(null);
			relatorioLaudoAIHVO.setCep(null);
		}

	}

	private void processarDadosPaciente(Integer codigoPac,
			RelatorioLaudoAIHVO relatorioLaudoAIHVO) {
		AipPacientes paciente = getPacienteFacade().obterAipPacientesPorChavePrimaria(codigoPac);

		relatorioLaudoAIHVO.setNomePaciente(paciente.getNome());
		if(paciente.getProntuario() != null && paciente.getProntuario() > 90000000){
			relatorioLaudoAIHVO.setProntuario(null);
		}else{
			relatorioLaudoAIHVO.setProntuario(paciente.getProntuario());
		}
		
		AinResponsaveisPaciente responsavel = internacaoFacade.obterResponsaveisPacientePorNome(paciente.getNome());
		
		relatorioLaudoAIHVO.setDataNascimento(paciente.getDtNascimento());
		relatorioLaudoAIHVO.setSexo(validarSexoPaciente(paciente)); 
		relatorioLaudoAIHVO.setCorPaciente(validarCorPaciente(paciente));
		relatorioLaudoAIHVO.setEtnia(validarEtnia(paciente));
		relatorioLaudoAIHVO.setNomeMae(paciente.getNomeMae());
		relatorioLaudoAIHVO.setTelefone(validarTelefonePaciente(paciente));
		relatorioLaudoAIHVO.setNomeResponsavelPaciente(recuperarResponsavelPaciente(responsavel));
	}

	private String recuperarResponsavelPaciente(AinResponsaveisPaciente responsavel) {
		return responsavel == null 
				|| responsavel.getResponsavelConta() == null 
				|| responsavel.getResponsavelConta().getNome() == null  ? "" : responsavel.getResponsavelConta().getNome();
	}

	private String validarTelefonePaciente(AipPacientes paciente) {
		return paciente.getDddFoneResidencial() 
				!= null ? paciente.getDddFoneResidencial() + "-" + paciente.getFoneResidencial() : paciente.getDddFoneRecado() 
				!= null ? paciente.getDddFoneRecado() + "-" + paciente.getFoneRecado() : "  ";
	}

	private String validarSexoPaciente(AipPacientes paciente) {
		return paciente.getSexo() != null ? paciente.getSexo().getDescricao() : null;
	}

	private String validarCorPaciente(AipPacientes paciente) {
		return paciente.getCor().getDescricao() != null ? paciente.getCor().getDescricao(): null;
	}

	private String validarEtnia(AipPacientes paciente) {
		return paciente.getEtnia() == null || paciente.getEtnia().getDescricao() == null ? "" : paciente.getEtnia().getDescricao();
	}

	private void processarConselhoProfissional(RapServidores servidorValida,
			RelatorioLaudoAIHVO relatorioLaudoAIHVO)
			throws ApplicationBusinessException, ApplicationBusinessException {
		
		Object[] respInternacao = null;
		/*
		 * --
		   -- busca nome, sigla e nome do conselho do profissional responsável pela internação
		   --
		 */
		if(servidorValida != null){
			respInternacao = getPrescricaoMedicaFacade().buscaConsProf(servidorValida);
//			if (servidorValida.getPessoaFisica() != null && servidorValida.getPessoaFisica().getCpf() != null) {
//				relatorioLaudoAIHVO.setCpfMedSolic(servidorValida.getPessoaFisica().getCpf().toString());
//			}
		}
		
		if(respInternacao != null && respInternacao[1] != null){//nome
			relatorioLaudoAIHVO
			.setNomeMedico(StringUtils.capitalize(respInternacao[1].toString()));
			if (respInternacao[3] != null) {
				relatorioLaudoAIHVO.setNroConselho(respInternacao[3].toString());
			}
			if (respInternacao[2] != null) {
				relatorioLaudoAIHVO.setConselho(respInternacao[2].toString());
			}
		}		
	}

	public RelatorioLaudoAIHSolicVO pesquisarLaudoAIHSolic(String materialSolicitado, Integer codigoPac, Integer matricula, Short vinCodigo) throws ApplicationBusinessException{
		RelatorioLaudoAIHSolicVO relatorioLaudoAIHSolicVO = new RelatorioLaudoAIHSolicVO();
		AghParametros parametroSituacaoPendente = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		if(parametroSituacaoPendente.getVlrTexto()!=null){
			relatorioLaudoAIHSolicVO.setNomeHospital(parametroSituacaoPendente.getVlrTexto());
		}
		parametroSituacaoPendente = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_CGC);
		if(parametroSituacaoPendente.getVlrTexto()!=null){
			relatorioLaudoAIHSolicVO.setCgcHospital(parametroSituacaoPendente.getVlrTexto());
		}
		AipPacientes paciente = pacienteFacade.obterPacientePorCodigo(codigoPac);		
		relatorioLaudoAIHSolicVO.setNomePaciente(paciente.getNome());
		
		List<ConselhoProfissionalServidorVO> registroMedico = iAmbulatorioFacade.obterRegistroMedico(matricula, vinCodigo);
	
		if(!registroMedico.isEmpty()){
			relatorioLaudoAIHSolicVO.setNomeMedico(registroMedico.get(0).getNome());
			relatorioLaudoAIHSolicVO.setCpfMedico(registroMedico.get(0).getCpf());
			relatorioLaudoAIHSolicVO.setCrmMedico(registroMedico.get(0).getCodigoConselho()); 
		}
		
		relatorioLaudoAIHSolicVO.setMaterialSolicitado(materialSolicitado);
		return relatorioLaudoAIHSolicVO;
	}
	
	
	public IAmbulatorioFacade getAmbulatorioFacade() {
		return iAmbulatorioFacade;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.iCadastrosBasicosInternacaoFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected VAinServInternaDAO getVAinServInternaDAO() {
		return vAinServInternaDAO;
	}

	protected IPacienteFacade getPacienteFacade() {
		return this.iPacienteFacade;
	}	
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return this.iPrescricaoMedicaFacade;
	}
		
	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return iCadastroPacienteFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
}
