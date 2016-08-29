package br.gov.mec.aghu.emergencia.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncExternoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.controlepaciente.service.IControlePacienteService;
import br.gov.mec.aghu.controlepaciente.vo.DadosSinaisVitaisVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.emergencia.dao.MamFluxogramaDAO;
import br.gov.mec.aghu.emergencia.dao.MamGravidadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamProtClassifRiscoDAO;
import br.gov.mec.aghu.emergencia.vo.FormularioEncExternoVO;
import br.gov.mec.aghu.emergencia.vo.SubFormularioSinaisVitaisVO;
import br.gov.mec.aghu.model.MamFluxograma;
import br.gov.mec.aghu.model.MamGravidade;
import br.gov.mec.aghu.model.MamProtClassifRisco;
import br.gov.mec.aghu.model.MamTrgEncExternos;
import br.gov.mec.aghu.model.MamTrgEncExternosId;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.prescricaomedica.service.IPrescricaoMedicaService;
import br.gov.mec.aghu.prescricaomedica.vo.PostoSaude;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class FormularioEncaminhamentoExternoON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3397049725484856633L;

	@EJB
	private IPacienteService pacienteService;
	
	@EJB
	private IControlePacienteService controlePacienteService;

	@EJB
	private IPrescricaoMedicaService prescricaoMedicaService;
	
	@Inject
	private MamTrgEncExternoDAO mamTrgEncExternoDAO;

	@Inject
	private MamGravidadeDAO mamGravidadeDAO;
	@Inject
	private MamTriagensDAO mamTriagensDAO;
	
	@Inject
	private MamFluxogramaDAO mamFluxogramaDAO; 

	@Inject
	private MamProtClassifRiscoDAO mamProtClassifRiscoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	public enum FormularioEncaminhamentoExternoONExceptionCode implements BusinessExceptionCode {
		
		MENSAGEM_SERVICO_INDISPONIVEL,PARAMETRO_IMP_FORM_NAO_ENCONTRADO;

	}
	
	public List<FormularioEncExternoVO> carregarDadosFormulario(Long triagem, Short seqpTriagem, String cidadeLocal) throws ApplicationBusinessException {
		try {
			
			MamTrgEncExternos encExterno = mamTrgEncExternoDAO.obterPorChavePrimaria(new MamTrgEncExternosId(triagem, seqpTriagem));
			MamTriagens mamTriagem = mamTriagensDAO.obterPorChavePrimaria(triagem);
			if(encExterno != null) {
				List<DadosSinaisVitaisVO> sinaisVitais = controlePacienteService.pesquisarUltimosSinaisVitaisPeloCodigoPaciente(encExterno.getTriagem().getPaciente().getCodigo());
				Paciente dadosEnderecoPaciente = pacienteService.obterDadosContatoPacientePeloCodigo(encExterno.getTriagem().getPaciente().getCodigo());
				
				FormularioEncExternoVO vo = new FormularioEncExternoVO();
				if(dadosEnderecoPaciente != null) {
					vo.setNome(dadosEnderecoPaciente.getNome());
					vo.setLogradouro(dadosEnderecoPaciente.getLogradouro());
					vo.setNroLogradouro(dadosEnderecoPaciente.getNroLogradouro());
					vo.setComplLogradouro(dadosEnderecoPaciente.getComplLogradouro());
					vo.setBairro(dadosEnderecoPaciente.getBairro());
					vo.setMunicipio(dadosEnderecoPaciente.getCidade());
					vo.setDddTelefone(dadosEnderecoPaciente.getDddTelefone());
					vo.setTelefone(dadosEnderecoPaciente.getTelefone());
				}
		
				if(sinaisVitais != null) {
					List<SubFormularioSinaisVitaisVO> listaSinaisVitais = new ArrayList<SubFormularioSinaisVitaisVO>();
					for(DadosSinaisVitaisVO sinal : sinaisVitais) {
						if(!DateUtil.entre(sinal.getDataHora(), DateUtil.adicionaHoras(mamTriagem.getDthrUltMvto(), -1), mamTriagem.getDthrUltMvto())) {
							continue;
						}	
							SubFormularioSinaisVitaisVO sinaisVitaisVO = new SubFormularioSinaisVitaisVO();
							if (StringUtils.isBlank(sinal.getUnidaMedida())) {
								sinaisVitaisVO.setCabecalho(sinal.getSigla());
							}else{
								sinaisVitaisVO.setCabecalho(sinal.getSigla() + '(' + sinal.getUnidaMedida()+')');
							}
							sinaisVitaisVO.setValor(sinal.getMedicaoFormatada());
							listaSinaisVitais.add(sinaisVitaisVO);
					}
					
					vo.setSinaisAferidos(listaSinaisVitais);
			
				}
				
				if(encExterno.getPssSeq() != null) {
					List<PostoSaude> listaPostos = prescricaoMedicaService.listarMpmPostoSaudePorSeqDescricao(encExterno.getPssSeq());
					if(listaPostos != null && !listaPostos.isEmpty()) {
						vo.setUnidSaudeExt(listaPostos.get(0).getDescricao());
						vo.setMunicioUnidSaudeExt(listaPostos.get(0).getCidade());
					}
				}
				
				popularDadosFormulario(vo, triagem, seqpTriagem);
				
				vo.setQueixaPrinipal(encExterno.getTriagem().getQueixaPrincipal());
				vo.setDataQueixa(encExterno.getTriagem().getDataQueixa());
				vo.setHoraQueixa(encExterno.getTriagem().getHoraQueixa());
				
				BigDecimal grvSeq = null;
				MamGravidade maiorGravidade = null;
				grvSeq = (BigDecimal) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_AGHU_GRAV_IMP_FORM_ENC_EXTERNO.toString(), "vlrNumerico");
				if(grvSeq != null) {
					maiorGravidade = mamGravidadeDAO.obterPorChavePrimaria(grvSeq.shortValue());
				}
				vo.setGrauGravidade(maiorGravidade != null ? maiorGravidade.getDescricao() : null);
				vo = carregarLabels(vo, cidadeLocal);
				List<FormularioEncExternoVO> vos = new ArrayList<>();
				vos.add(vo);
				return vos;
			}			
			return null;
		} catch (ServiceException | ApplicationBusinessException e) {
			throw new ApplicationBusinessException(FormularioEncaminhamentoExternoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}
	
	private FormularioEncExternoVO  carregarLabels(FormularioEncExternoVO vo, String cidadeLocal){
		vo.setLabelNome("Nome do Cidadão: "+vo.getNome());
		vo.setLabelEndereco("Endereço: "+vo.getLogradouro());
		vo.setLabelUnidadeReferencia("Unidade Referenciada em "+vo.getMunicioUnidSaudeExt());
		vo.setLabelClassificacaoRisco("Classificação de Risco por Protocolo de "+vo.getProtocolo());
		vo.setLabelFluxograma("Fluxograma Utilizado: "+vo.getFluxograma());
		vo.setLabelAtencaoPrimaria(vo.getGrauGravidade()+", informar motivo do não referenciamento para atenção primária: ");
		vo.setLabelPacienteSemReferencia("Paciente não tem referência em "+cidadeLocal+". Qual cidade de origem? "+vo.getMunicipio());
		vo.setLabelCidadeOrigem("Qual cidade de origem? "+vo.getMunicipio());
		vo.setLabelCidadeTrabalha1("Se morador de outra cidade, trabalha em " +cidadeLocal+"?");
		vo.setLabelPacientesClassificacao("(Para os pacientes classificados com "+vo.getGrauGravidade()+" pelo "+vo.getProtocolo()+")");
		return vo;
	}
	
	private void popularDadosFormulario(FormularioEncExternoVO vo, Long triagem, Short seqpTriagem) throws ApplicationBusinessException {
		List<MamGravidade> listaGravidade = mamGravidadeDAO.pesquisarGravadadePorTriagemEncExterno(triagem, seqpTriagem);
		if(listaGravidade != null && !listaGravidade.isEmpty()) {
			vo.setClassificacaoGravidade(listaGravidade.get(0).getDescricao());
		}
		
		List<MamFluxograma> listaFluxograma = mamFluxogramaDAO.pesquisarFluxogramaPorTriagemEncExterno(triagem, seqpTriagem);
		if(listaFluxograma != null && !listaFluxograma.isEmpty()) {
			vo.setFluxograma(listaFluxograma.get(0).getDescricao());
		}
		
		List<MamProtClassifRisco> listaClassRisco = mamProtClassifRiscoDAO.pesquisarProtClassifRiscoPorTriagemEncExterno(triagem, seqpTriagem);
		if(listaClassRisco != null && !listaClassRisco.isEmpty()) {
			vo.setProtocolo(listaClassRisco.get(0).getDescricao());
		}
	}
	
	public DominioSimNao obterParametroFormularioExterno() throws ApplicationBusinessException {
		String paramString = (String) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_AGHU_IMP_FORM_ENC_EXTERNO.toString(), "vlrTexto");
		
		if (StringUtils.isNotBlank(paramString)) {
			 if (paramString.equalsIgnoreCase("S")){
				 return DominioSimNao.S;
			 } else {
				 return DominioSimNao.N;
			 }
		} else {
			throw new ApplicationBusinessException(FormularioEncaminhamentoExternoONExceptionCode.PARAMETRO_IMP_FORM_NAO_ENCONTRADO);
		}
	}
	
	@Override
	protected Log getLogger() {
		return null;
	}
}
