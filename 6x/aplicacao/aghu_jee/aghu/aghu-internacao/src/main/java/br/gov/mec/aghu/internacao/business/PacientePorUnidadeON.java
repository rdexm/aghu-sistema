package br.gov.mec.aghu.internacao.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioPacientesUnidade;
import br.gov.mec.aghu.internacao.dao.AinAcompanhantesInternacaoDAO;
import br.gov.mec.aghu.internacao.vo.PacienteUnidadeFuncionalVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcompanhantesInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.prescricaomedica.vo.AghAtendimentosVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para o
 * relatório de pacientes por unidade funcional.
 * 
 * @author tfelini
 * 
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class PacientePorUnidadeON extends BaseBusiness {
	
	private static final long serialVersionUID = -4236906776290035033L;
	
	private enum PacientePorUnidadeONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO;
	}

	private static final Log LOG = LogFactory.getLog(PacientePorUnidadeON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private AinAcompanhantesInternacaoDAO ainAcompanhantesInternacaoDAO;

	@Secure("#{s:hasPermission('relatorio','pacientesUnidadeFuncional')}")
	public List<PacienteUnidadeFuncionalVO> pesquisa(AghUnidadesFuncionais unidadeFuncional, DominioOrdenacaoRelatorioPacientesUnidade ordenacao) {

		final List<AghUnidadesFuncionais> resUf = getAghuFacade().pesquisaUnidadesFuncionais(unidadeFuncional);
		final List<AghAtendimentosVO> resAt = getAghuFacade().pesquisaAtendimentos(unidadeFuncional, ordenacao);
		
		return montarRelatorioPacientePorUnidade(resUf, resAt, null);
	}

	@Secure("#{s:hasPermission('relatorio','pacientesUnidadeFuncional')}")
	public Long pesquisaAtendimentosCount(final AghUnidadesFuncionais unidadeFuncional) {
		return  getAghuFacade().pesquisaAtendimentosCount(unidadeFuncional);
	}
	
	@Secure("#{s:hasPermission('relatorio','pacientesUnidadeFuncional')}")
	public List<PacienteUnidadeFuncionalVO> pesquisaAcompanhantes(AghUnidadesFuncionais unidadeFuncional, DominioOrdenacaoRelatorioPacientesUnidade ordenacao) throws ApplicationBusinessException {
		List<AghUnidadesFuncionais> resUf = getAghuFacade().pesquisaUnidadesFuncionais(unidadeFuncional);

		final List<AghAtendimentosVO> resAt = getAghuFacade().pesquisaAtendimentos(unidadeFuncional, ordenacao);
		if (unidadeFuncional != null && (resAt == null || resAt.isEmpty())) {
			throw new ApplicationBusinessException(PacientePorUnidadeONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO);
		}
		final List<Integer> intSeqs = new LinkedList<>();
		for (AghAtendimentosVO aghAtendimentosVO : resAt) {
			intSeqs.add(aghAtendimentosVO.getIntSeq());
		}
		
		final Map<Integer, List<String>> mapAcompanhantes = new HashMap<>();
		
		if(!intSeqs.isEmpty()){
			final List<AinAcompanhantesInternacao> acompanhantes = ainAcompanhantesInternacaoDAO.obterAcompanhantesInternacao(intSeqs);
			
			if(!acompanhantes.isEmpty()){
				Integer intSeq = acompanhantes.get(0).getId().getIntSeq();
						
				List<String> nomeAcompanhantes = new ArrayList<>();
				for (AinAcompanhantesInternacao ainAcompanhantesInternacao : acompanhantes) {
			
					if(CoreUtil.igual(intSeq, ainAcompanhantesInternacao.getId().getIntSeq())){
						nomeAcompanhantes.add(ainAcompanhantesInternacao.getNome());
						
					} else {
						nomeAcompanhantes = new ArrayList<>();
						intSeq = ainAcompanhantesInternacao.getId().getIntSeq();
						nomeAcompanhantes.add(ainAcompanhantesInternacao.getNome());
					}
					mapAcompanhantes.put(intSeq, nomeAcompanhantes);
				}
			}
		}
		
		if (unidadeFuncional != null && (resAt == null || resAt.isEmpty())) {
			throw new ApplicationBusinessException(PacientePorUnidadeONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO);
		}

		return montarRelatorioPacientePorUnidade(resUf, resAt, mapAcompanhantes);
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private List<PacienteUnidadeFuncionalVO> montarRelatorioPacientePorUnidade( final List<AghUnidadesFuncionais> itUf, final List<AghAtendimentosVO> itAt,
																				final Map<Integer, List<String>> mapAcompanhantes) {

		List<PacienteUnidadeFuncionalVO> lista = new ArrayList<PacienteUnidadeFuncionalVO>(0);
		
		for(AghUnidadesFuncionais unidadeFuncionalIt : itUf){
			if ( !unidadeFuncionalIt.isAtivo() ) {
				continue;
			}
			PacienteUnidadeFuncionalVO vo = new PacienteUnidadeFuncionalVO();

			if (unidadeFuncionalIt.getSeq() != null) {
				vo.setUnfFilhaSeq(unidadeFuncionalIt.getSeq().intValue());
			}
			if (unidadeFuncionalIt.getAndarAlaDescricao() != null) {
				vo.setUnfFilhaDescricao(unidadeFuncionalIt.getAndarAlaDescricao());
			}
			if (unidadeFuncionalIt.getUnfSeq() != null && unidadeFuncionalIt.getUnfSeq().getSeq() != null) {
				vo.setLblUnidade("Unidade Mãe: ");
				vo.setUnfMaeSeq(unidadeFuncionalIt.getUnfSeq().getSeq().intValue());
			}
			if (unidadeFuncionalIt.getUnfSeq() != null && unidadeFuncionalIt.getUnfSeq().getAndarAlaDescricao() != null) {
				vo.setUnfMaeDescricao(unidadeFuncionalIt.getUnfSeq().getAndarAlaDescricao());
			}
			lista.add(vo);
			
			for(AghAtendimentosVO atendimentoVO : itAt){
				if (CoreUtil.igual(atendimentoVO.getUnfSeq(), unidadeFuncionalIt.getSeq()) ){
					vo = new PacienteUnidadeFuncionalVO();

					vo.setEstadoSaude(atendimentoVO.getEstadoSaude());
					
					if (unidadeFuncionalIt.getSeq() != null) {
						vo.setUnfFilhaSeq(unidadeFuncionalIt.getSeq().intValue());
					}
					if (unidadeFuncionalIt.getAndarAlaDescricao() != null) {
						vo.setUnfFilhaDescricao(unidadeFuncionalIt.getAndarAlaDescricao());
					}
					if (unidadeFuncionalIt.getUnfSeq() != null && unidadeFuncionalIt.getUnfSeq().getSeq() != null) {
						vo.setUnfMaeSeq(unidadeFuncionalIt.getUnfSeq().getSeq().intValue());
					}
					if (unidadeFuncionalIt.getUnfSeq() != null && unidadeFuncionalIt.getUnfSeq().getAndarAlaDescricao() != null) {
						vo.setUnfMaeDescricao(unidadeFuncionalIt.getUnfSeq().getAndarAlaDescricao());
					}
					
					vo.setLtoLtoId(atendimentoVO.getLtoId());
					vo.setNomePaciente(atendimentoVO.getNomePaciente());
					
					AipPacientes pac = new AipPacientes(atendimentoVO.getCodigoPaciente(), atendimentoVO.getDtNascimentoPaciente());
					vo.setIdadePaciente(pac.getIdade());
					
					vo.setConNumero(atendimentoVO.getConNumero());
					
					if (atendimentoVO.getProntuario() != null) {
						// Tranforma número de 124567 para 0012456/7
						String prontAux = atendimentoVO.getProntuario().toString();
						prontAux = prontAux.substring(0, prontAux.length() - 1)
								+ "/" + prontAux.charAt(prontAux.length() - 1);
						vo.setProntuario(StringUtils.leftPad(prontAux, 9, '0') );
					}
					
					if (atendimentoVO.getDthrInicio() != null) {
						vo.setDataInicioAtendimento(atendimentoVO.getDthrInicio());
					}
					
					if (atendimentoVO.getSiglaEspecialidade() != null) {
						vo.setSiglaEspecialidade(atendimentoVO.getSiglaEspecialidade());
					}
					
					if (atendimentoVO.getSenhaAutorizada() != null){
						vo.setSenha(atendimentoVO.getSenhaAutorizada());
					}
					
					if(mapAcompanhantes != null && mapAcompanhantes.containsKey(atendimentoVO.getIntSeq())){
						vo.setAcompanhantes(mapAcompanhantes.get(atendimentoVO.getIntSeq()));
					}
					
					lista.add(vo);
				}
			}
		}
		return lista;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public AinAcompanhantesInternacaoDAO getAinAcompanhantesInternacaoDAO() {
		return ainAcompanhantesInternacaoDAO;
	}
}
