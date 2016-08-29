package br.gov.mec.aghu.exames.business;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSecaoConfiguravel;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.dao.AelDescMaterialApsDAO;
import br.gov.mec.aghu.exames.dao.AelDiagnosticoApsDAO;
import br.gov.mec.aghu.exames.dao.AelDiagnosticoLaudosDAO;
import br.gov.mec.aghu.exames.dao.AelLaminaApsDAO;
import br.gov.mec.aghu.exames.dao.AelMacroscopiaApsDAO;
import br.gov.mec.aghu.exames.dao.AelSecaoConfExamesDAO;
import br.gov.mec.aghu.exames.dao.AelTopografiaLaudosDAO;
import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.model.AelDiagnosticoAps;
import br.gov.mec.aghu.model.AelSecaoConfExames;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class LaudoUnicoTecnicaRN extends BaseBusiness  {

	private static final long serialVersionUID = -900769161446661185L;

	public enum LaudoUnicoTecnicaRNExceptionCode implements BusinessExceptionCode {
		ERRO_SECAO_OBRIGATORIA;
	}
	

	private static final Log LOG = LogFactory.getLog(LaudoUnicoTecnicaRN.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private AelSecaoConfExamesDAO aelSecaoConfExamesDAO;
	
	@Inject
	private AelMacroscopiaApsDAO aelMacroscopiaApsDAO;
	
	@Inject
	private AelDiagnosticoApsDAO aelDiagnosticoApsDAO;
	
	@Inject
	private AelDescMaterialApsDAO aelDescMaterialApsDAO;
	
	@Inject
	private AelTopografiaLaudosDAO aelTopografiaLaudosDAO;

	@Inject
	private AelDiagnosticoLaudosDAO aelDiagnosticoLaudosDAO;

	@Inject
	private AelLaminaApsDAO aelLaminaApsDAO;

	public void buscaSecoesConfiguracaoObrigatorias(TelaLaudoUnicoVO telaLaudoVO, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		List<AelSecaoConfExames> result = getAelSecaoConfExamesDAO().buscaSecoesConfiguracaoObrigatorias(domain, telaLaudoVO.getAelAnatomoPatologico().getLu2VersaoConf(), telaLaudoVO.getAelAnatomoPatologico().getConfigExame().getSeq());
		validaSecoesConfiguracaoObrigatorias(result, telaLaudoVO, domain);
	}

	protected Set<DominioSecaoConfiguravel> getSecoesObrigatorias(List<AelSecaoConfExames> results) {
		Set<DominioSecaoConfiguravel> secoesObrigatorias = new HashSet<DominioSecaoConfiguravel>();
		for (AelSecaoConfExames result : results) {
			secoesObrigatorias.add(result.getDescricao());
		}
		return secoesObrigatorias;
	}

	private void validaSecoesConfiguracaoObrigatorias(List<AelSecaoConfExames> buscaSecoesConfiguracaoObrigatorias, TelaLaudoUnicoVO telaLaudoVO, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		if (buscaSecoesConfiguracaoObrigatorias.size() > 0) {
			Set<DominioSecaoConfiguravel> secoesObrigatorias = getSecoesObrigatorias(buscaSecoesConfiguracaoObrigatorias);
			final Map<DominioSecaoConfiguravel, AelSecaoConfExames> erros = avaliaSecoesConfiguracaoObrigatorias(buscaSecoesConfiguracaoObrigatorias,
					telaLaudoVO, secoesObrigatorias, domain);
			if (!erros.isEmpty()) {
				throw new ApplicationBusinessException(LaudoUnicoTecnicaRNExceptionCode.ERRO_SECAO_OBRIGATORIA, getDomainDescricao(domain),
						getDescricaoParaException(erros.keySet()));
			}
		}
	}

	private Map<DominioSecaoConfiguravel, AelSecaoConfExames> avaliaSecoesConfiguracaoObrigatorias(
			List<AelSecaoConfExames> buscaSecoesConfiguracaoObrigatorias, TelaLaudoUnicoVO telaLaudoVO, Set<DominioSecaoConfiguravel> secoesObrigatorias,
			DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		final Map<DominioSecaoConfiguravel, AelSecaoConfExames> erros = new HashMap<DominioSecaoConfiguravel, AelSecaoConfExames>();
		for (final AelSecaoConfExames secaoConfExame : buscaSecoesConfiguracaoObrigatorias) {
			final DominioSecaoConfiguravel erro = avaliaDescricao(secaoConfExame.getDescricao(), telaLaudoVO, secoesObrigatorias, domain);
			if (erro != null) {
				erros.put(erro, secaoConfExame);
			}
		}
		return erros;
	}

	private DominioSecaoConfiguravel avaliaDescricao(DominioSecaoConfiguravel descricao, TelaLaudoUnicoVO telaLaudoVO,
			Set<DominioSecaoConfiguravel> secoesObrigatorias, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		switch (descricao) {
		case LMA:
			try {
				avalidaMacroscopia(telaLaudoVO, secoesObrigatorias, domain);
			} catch (ApplicationBusinessException e) {
				return DominioSecaoConfiguravel.LMA;
			}
			break; // C3
		case LDI:
			try {
				avaliaMargemCirurgica(telaLaudoVO, secoesObrigatorias, domain);
			} catch (ApplicationBusinessException e) {
				return DominioSecaoConfiguravel.LDI;
			}
			break; // C4
		case LDE:
			try {
				avaliaDiagnosticoAPS(telaLaudoVO, secoesObrigatorias, domain);
			} catch (ApplicationBusinessException e) {
				return DominioSecaoConfiguravel.LDE;
			}
			break; // C5
		case LDM:
			try {
				avaliaDescricaoMaterial(telaLaudoVO, secoesObrigatorias, domain);
			} catch (ApplicationBusinessException e) {
				return DominioSecaoConfiguravel.LDM;
			}
			break; // C6
		case CTO:
			try {
				avaliaTopografia(telaLaudoVO, secoesObrigatorias, domain);
			} catch (ApplicationBusinessException e) {
				return DominioSecaoConfiguravel.CTO;
			}
			break; // C7
		case CDI:
			try {
				avaliaDiagnosticoLaudos(telaLaudoVO, secoesObrigatorias, domain);
			} catch (ApplicationBusinessException e) {
				return DominioSecaoConfiguravel.CDI;
			}

			break; // C8
		case IBL:
			try {
				avaliaIndiceBlocos(telaLaudoVO, secoesObrigatorias, domain);
			} catch (ApplicationBusinessException e) {
				return DominioSecaoConfiguravel.IBL;
			}
			break; // C9
		default:
			return null;
		}
		return null;
	}

	private void avalidaMacroscopia(TelaLaudoUnicoVO telaLaudoVO, Set<DominioSecaoConfiguravel> secoesObrigatorias, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		Long count = getAelMacroscopiaApsDAO().listarMacroscopiasAPSCount(telaLaudoVO);
		if(hasZeroResults(count)){
			throw new ApplicationBusinessException(LaudoUnicoTecnicaRNExceptionCode.ERRO_SECAO_OBRIGATORIA, getDomainDescricao(domain), getDescricaoParaException(secoesObrigatorias));
		}
	}

	protected String getDescricaoParaException(Set<DominioSecaoConfiguravel> secoesObrigatorias) {
		StringBuilder secoes = new StringBuilder();
		String separador = "";
		for (DominioSecaoConfiguravel dominioSecaoConfiguravel : secoesObrigatorias) {
			secoes.append(separador).append(dominioSecaoConfiguravel.getDescricao()).append('(').append(dominioSecaoConfiguravel.getAba().getDescricao()).append(')');
			separador = ", ";
		}
		return secoes.toString();
	}

	private void avaliaMargemCirurgica(TelaLaudoUnicoVO telaLaudoVO, Set<DominioSecaoConfiguravel> secoesObrigatorias, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		AelDiagnosticoAps result = getAelDiagnosticoApsDAO().listarDiagnosticoAPS(telaLaudoVO);
		if(result != null){
			avalidaNeoplasiaMaligna(result, secoesObrigatorias, domain);
		}else{
			throw new ApplicationBusinessException(LaudoUnicoTecnicaRNExceptionCode.ERRO_SECAO_OBRIGATORIA, getDomainDescricao(domain), getDescricaoParaException(secoesObrigatorias));
		}
	}

	private void avaliaDiagnosticoAPS(TelaLaudoUnicoVO telaLaudoVO, Set<DominioSecaoConfiguravel> secoesObrigatorias, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		Long count = getAelDiagnosticoApsDAO().listarDiagnosticoAPSCount(telaLaudoVO);
		if(hasZeroResults(count)){
			throw new ApplicationBusinessException(LaudoUnicoTecnicaRNExceptionCode.ERRO_SECAO_OBRIGATORIA, getDomainDescricao(domain), getDescricaoParaException(secoesObrigatorias));
		}
	}

	private void avaliaDescricaoMaterial(TelaLaudoUnicoVO telaLaudoVO, Set<DominioSecaoConfiguravel> secoesObrigatorias, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		Long count = getAelDescMaterialApsDAO().listarDescMaterialCount(telaLaudoVO);
		if(hasZeroResults(count)){
			throw new ApplicationBusinessException(LaudoUnicoTecnicaRNExceptionCode.ERRO_SECAO_OBRIGATORIA, getDomainDescricao(domain), getDescricaoParaException(secoesObrigatorias));
		}
	}

	private void avaliaTopografia(TelaLaudoUnicoVO telaLaudoVO, Set<DominioSecaoConfiguravel> secoesObrigatorias, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		Long count = getAelTopografiaLaudosDAO().listarTopografiaLaudosCount(telaLaudoVO);
		if(hasZeroResults(count)){
			throw new ApplicationBusinessException(LaudoUnicoTecnicaRNExceptionCode.ERRO_SECAO_OBRIGATORIA, getDomainDescricao(domain), getDescricaoParaException(secoesObrigatorias));
		}
	}

	private void avaliaDiagnosticoLaudos(TelaLaudoUnicoVO telaLaudoVO, Set<DominioSecaoConfiguravel> secoesObrigatorias, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		Long count = getAelDiagnosticoLaudosDAO().listarDisgnosticosLaudosCount(telaLaudoVO);
		if(hasZeroResults(count)){
			throw new ApplicationBusinessException(LaudoUnicoTecnicaRNExceptionCode.ERRO_SECAO_OBRIGATORIA, getDomainDescricao(domain), getDescricaoParaException(secoesObrigatorias));
		}
	}

	private void avaliaIndiceBlocos(TelaLaudoUnicoVO telaLaudoVO, Set<DominioSecaoConfiguravel> secoesObrigatorias, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		Long count = getAelLaminaApsDAO().listarIndiceBlocoCount(telaLaudoVO);
		if(hasZeroResults(count)){
			throw new ApplicationBusinessException(LaudoUnicoTecnicaRNExceptionCode.ERRO_SECAO_OBRIGATORIA, getDomainDescricao(domain), getDescricaoParaException(secoesObrigatorias));
		}
	}

	private void avalidaNeoplasiaMaligna(AelDiagnosticoAps result, Set<DominioSecaoConfiguravel> secoesObrigatorias, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		if(result.getNeoplasiaMaligna() == null){
			throw new ApplicationBusinessException(LaudoUnicoTecnicaRNExceptionCode.ERRO_SECAO_OBRIGATORIA, getDomainDescricao(domain), getDescricaoParaException(secoesObrigatorias));
		}else if(DominioSimNao.S.equals(result.getNeoplasiaMaligna())){
			avaliaBiopsia(result, secoesObrigatorias, domain);
		}
	}

	private void avaliaBiopsia(AelDiagnosticoAps result, Set<DominioSecaoConfiguravel> secoesObrigatorias, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		if(result.getBiopsia() == null){
			throw new ApplicationBusinessException(LaudoUnicoTecnicaRNExceptionCode.ERRO_SECAO_OBRIGATORIA, getDomainDescricao(domain), getDescricaoParaException(secoesObrigatorias));
		}else if(DominioSimNao.N.equals(result.getBiopsia())){
			avaliaMargemComprometida(result, secoesObrigatorias, domain);
		}
	}

	private void avaliaMargemComprometida(AelDiagnosticoAps result, Set<DominioSecaoConfiguravel> secoesObrigatorias, DominioSituacaoExamePatologia domain) throws ApplicationBusinessException {
		if(result.getMargemComprometida() == null){
			throw new ApplicationBusinessException(LaudoUnicoTecnicaRNExceptionCode.ERRO_SECAO_OBRIGATORIA, getDomainDescricao(domain), getDescricaoParaException(secoesObrigatorias));
		}
	}
	
	private String getDomainDescricao(DominioSituacaoExamePatologia domain) {
		if(domain == null){
			return DominioSituacaoExamePatologia.LA.getDescricao();
		}else{
			return domain.getDescricao();
		}
	}

	protected boolean hasZeroResults(Long count) {
		return count == 0;
	}

	public AelSecaoConfExamesDAO getAelSecaoConfExamesDAO(){
		return aelSecaoConfExamesDAO;
	}

	public AelMacroscopiaApsDAO getAelMacroscopiaApsDAO(){
		return aelMacroscopiaApsDAO;
	}

	public AelDiagnosticoApsDAO getAelDiagnosticoApsDAO(){
		return aelDiagnosticoApsDAO;
	}

	public AelDescMaterialApsDAO getAelDescMaterialApsDAO(){
		return aelDescMaterialApsDAO;
	}

	public AelTopografiaLaudosDAO getAelTopografiaLaudosDAO(){
		return aelTopografiaLaudosDAO;
	}

	public AelDiagnosticoLaudosDAO getAelDiagnosticoLaudosDAO(){
		return aelDiagnosticoLaudosDAO;
	}

	public AelLaminaApsDAO getAelLaminaApsDAO(){
		return aelLaminaApsDAO;
	}

	public Boolean habilitaBotaoTecnica(TelaLaudoUnicoVO telaLaudoVO, DominioSituacaoExamePatologia domain) {
		return getAelSecaoConfExamesDAO().verificaBotoesTecnica(domain, telaLaudoVO.getAelAnatomoPatologico().getLu2VersaoConf(), telaLaudoVO.getAelAnatomoPatologico().getConfigExame().getSeq());
	}
}
