package br.gov.mec.aghu.emergencia.business;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.ambulatorio.service.IAmbulatorioService;
import br.gov.mec.aghu.ambulatorio.vo.BoletimAtendimento;
import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.controlepaciente.service.IControlePacienteService;
import br.gov.mec.aghu.controlepaciente.vo.DadosSinaisVitaisVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.emergencia.vo.BoletimAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.DadosAcolhimentoBoletimAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.SubFormularioSinaisVitaisVO;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class BoletimAtentimentoON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1234253788833064143L;

	@EJB
	private IAmbulatorioService ambulatorioService;
	
	@EJB
	private IConfiguracaoService configuracaoService;

	@EJB
	private IPacienteService pacienteService;
	
	@EJB
	private IControlePacienteService controlePacienteService;

	@Inject
	private MamTrgEncInternoDAO mamTrgEncInternoDAO;
	
	public enum BoletimAtentimentoONExceptionCode implements BusinessExceptionCode {
		
		MENSAGEM_SERVICO_INDISPONIVEL;

	}
	
	public List<BoletimAtendimentoVO> carregarDadosBoletim(Integer consulta) throws ApplicationBusinessException {
		try {
			Especialidade especialidade	= configuracaoService.buscarEspecialidadePorConNumero(consulta);		
					BoletimAtendimento boletim = ambulatorioService.obterBoletimAtendimentoPelaConsulta(consulta);
			List<DadosSinaisVitaisVO> sinaisVitais = controlePacienteService.pesquisarUltimosSinaisVitaisPelaConsulta(consulta);
			Paciente dadosPaciente = pacienteService.obterDadosPacientePelaConsulta(consulta);
			
			BoletimAtendimentoVO vo = new BoletimAtendimentoVO();
			try {
				BeanUtils.copyProperties(vo, boletim);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new ApplicationBusinessException(BoletimAtentimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
			} catch (Exception e) {
				throw new ApplicationBusinessException(BoletimAtentimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
			}
			
			if(especialidade != null) {
				vo.setNomeEspecialidade(especialidade.getNomeEspecialidade());
			}
			
			if(dadosPaciente != null) {
				vo.setAltura(dadosPaciente.getAltura());
				vo.setPeso(dadosPaciente.getPeso());
				vo.setIdade(dadosPaciente.getIdade());
			}
			
			if(sinaisVitais != null) {
				List<SubFormularioSinaisVitaisVO> listaSinaisVitais = new ArrayList<SubFormularioSinaisVitaisVO>();
				for(DadosSinaisVitaisVO sinal : sinaisVitais) {	
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
			
			vo.setProtocolo(mamTrgEncInternoDAO.obterProtocolo(consulta));
			
			DadosAcolhimentoBoletimAtendimentoVO dadosClassificacao = mamTrgEncInternoDAO.obterDadosDaClassificacao(consulta);
			vo.setQueixaPrincipal(dadosClassificacao.getQueixaPrincipal());
			vo.setGravidade(dadosClassificacao.getGravidade());
			vo.setDescritor(dadosClassificacao.getDescritor());
			vo.setFluxograma(dadosClassificacao.getFluxograma());
			
			List<BoletimAtendimentoVO> vos = new ArrayList<>();
			vos.add(vo);
			
			return vos;
		} catch (ServiceException | ApplicationBusinessException e) {
			throw new ApplicationBusinessException(BoletimAtentimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}

	@Override
	protected Log getLogger() {
		return null;
	}

}
