package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndCutanea;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndGangPerif;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndGenitoUrinaria;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndLaringea;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndMeningite;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndMiliar;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndOcular;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndOssea;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndOutraExtraPulmonar;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndPleural;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.MpmNotificacaoTb;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmNotificacaoTbDAO;
import br.gov.mec.aghu.prescricaomedica.vo.SinamVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AipCidades;

/**
 * RN de #45060: Complementacao da estória confirmar prescrição (#883) -
 * Formulário do SINAM
 * 
 * @author aghu
 *
 */
@Stateless
public class CadastroSinamRN extends BaseBusiness {

	private static final long serialVersionUID = -1591818903372257934L;

	private static final Log LOG = LogFactory.getLog(CadastroSinamRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private MpmNotificacaoTbDAO mpmNotificacaoTbDAO;

	@EJB
	private ConsistePreenchimentoRN consistePreenchimentoRN;
	
	@EJB
	private MpmNotificacaoTbRN mpmNotificacaoTbRN;

	/**
	 * Gravar: Atualiza somente com os campos editáveis na tela
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	public void gravarFormularioSinam(SinamVO vo) throws ApplicationBusinessException {
		MpmNotificacaoTb ntb = prepararObjeto(vo);
		
		// Chamada da PROCEDURE MPMP_CONSISTE_PREENCHIMENTO
		consistePreenchimentoRN.consistirPreenchimento(ntb);
	}

	/**
	 * Verifica as opções que foram marcadas na tela e converte para o
	 * respectivo valor padrão de domínio
	 * 
	 * @param vo
	 * @param ntb
	 */
	private void converterValoExtrapulmonar(final SinamVO vo, final MpmNotificacaoTb ntb) {
		if (vo.isPleural()) {
			ntb.setIndPleural(DominioNotificacaoTuberculoseIndPleural.PLEURAL);
		}
		if (vo.isGangPerif()) {
			ntb.setIndGangPerif(DominioNotificacaoTuberculoseIndGangPerif.GANG_PERIF);
		}
		if (vo.isGeniturinaria()) {
			ntb.setIndGenitoUrinaria(DominioNotificacaoTuberculoseIndGenitoUrinaria.PLEURAL);
		}
		if (vo.isOssea()) {
			ntb.setIndOssea(DominioNotificacaoTuberculoseIndOssea.OSSEA);
		}
		if (vo.isOcular()) {
			ntb.setIndOcular(DominioNotificacaoTuberculoseIndOcular.OCULAR);
		}
		if (vo.isMiliar()) {
			ntb.setIndMiliar(DominioNotificacaoTuberculoseIndMiliar.MILIAR);
		}
		if (vo.isMeningite()) {
			ntb.setIndMeningite(DominioNotificacaoTuberculoseIndMeningite.MENINGITE);
		}
		if (vo.isCutanea()) {
			ntb.setIndCutanea(DominioNotificacaoTuberculoseIndCutanea.CUTANEA);
		}
		if (vo.isLaringea()) {
			ntb.setIndLaringea(DominioNotificacaoTuberculoseIndLaringea.LARINGEA);
		}
	}
	
	private MpmNotificacaoTb prepararObjeto(SinamVO vo) {
		MpmNotificacaoTb ntb = this.mpmNotificacaoTbDAO.obterPorChavePrimaria(vo.getSeq());

		/*
		 * Seta atributos dos dados gerais
		 */
		ntb.setDtNotificacao(vo.getDataNotificacao());
		ntb.setDtDiagnostico(vo.getDataDiagnostico());
		
		if (vo.getUfResidencia() != null) {
			AipUfs aipUf = this.pacienteFacade.obterAipUfsPorChavePrimaria(vo.getUfResidencia());
			ntb.setUf(aipUf);
		}
		
		ntb.setMunicipioNotificacao(vo.getMunicipioNotificacao());
		ntb.setUnidadeDeSaude(vo.getUnidadeSaude());
		ntb.setCnes(vo.getCodigo());

		/*
		 * Seta atributos da notificação individual
		 */

		ntb.setIndGestante(vo.getGestante());
		ntb.setEscolaridade(vo.getEscolaridade());

		/*
		 * Seta atributos dos dados individuais
		 */
		/*
		 * Seta atributos dos dados complementares do caso
		 */
		ntb.setTipoEntrada(vo.getTipoEntrada());

		ntb.setForma(vo.getForma());

		ntb.setIndAids(vo.getAids());
		ntb.setIndDiabetes(vo.getDiabetes());
		ntb.setIndDoencaMental(vo.getDoencaMental());
		ntb.setIndAlcoolismo(vo.getAlcoolismo());
		ntb.setIndDrogasIlicitas(vo.getUsoDrogasIlicitas());

		converterValoExtrapulmonar(vo, ntb);

		if (vo.getOutraExtrapulmonar() != null) {
			ntb.setIndOutraExtrapulmonar(DominioNotificacaoTuberculoseIndOutraExtraPulmonar.OUTRAS_NOVA_VERSAO);
			ntb.setDescrOutraExtrapulmonar(vo.getDescrOutraExtrapulmonar());
		} else if (StringUtils.isNotBlank(vo.getDescrOutraExtrapulmonar())) {
			ntb.setIndOutraExtrapulmonar(DominioNotificacaoTuberculoseIndOutraExtraPulmonar.OUTRAS_NOVA_VERSAO);
			ntb.setDescrOutraExtrapulmonar(vo.getDescrOutraExtrapulmonar());
		}

		ntb.setBaciloscopiaEscarro(vo.getBaciloscopiaEscarro());
		ntb.setRaioxTorax(vo.getRadiografiaTorax());
		ntb.setDescOutroAgravo(vo.getDescricaoAgravo());
		ntb.setCulturaEscarro(vo.getCultura());
		ntb.setHiv(vo.getHiv());
		ntb.setHistopatologia(vo.getHistopatologia());
		ntb.setDtInicioTratAtual(vo.getDataInicioTratamentoAtual());
		
		if (vo.getNroProntuario() != null) {					
			ntb.setProntuario(Integer.valueOf(vo.getNroProntuario().replace("/", "")));
		}
		
		if (vo.getCodigoIbge() != null) {
			AipCidades cidade = this.pacienteFacade.obterCidadePorChavePrimaria(vo.getCodigoIbge());
			ntb.setCidade(cidade);
		}
		
		//Dados paciente
		ntb.setNomePaciente(vo.getNomePaciente());
		ntb.setDtNascimento(vo.getDataNascimento());
		ntb.setIdade(vo.getIdade());
		
		ntb.setEspecIdade(vo.getMediaIdade());
		ntb.setSexo(vo.getSexo());
		ntb.setRaca(vo.getRaca());
		ntb.setNroCartaoSus(vo.getNumeroCartaoSus());
		ntb.setNomeMae(vo.getNomeMae());
		ntb.setLogradouro(vo.getLogradouro());
		ntb.setNumeroLogradouro(vo.getNumero().toString());
		ntb.setCodigoLogradouro(vo.getCodigoLogradouro());
		ntb.setComplLogradouro(vo.getComplemento());
		ntb.setPontoReferencia(vo.getPontoReferencia());
		ntb.setMunicipioResidencia(vo.getMunicipioResidencia());
		ntb.setDistrito(vo.getDistrito());
		ntb.setBairro(vo.getBairro());
		ntb.setCep(vo.getCep());
		ntb.setDddTelefone(vo.getDddTelefone());
		ntb.setNumeroTelefone(vo.getNumeroTelefone());
		ntb.setZona(vo.getZona());
		ntb.setPais(vo.getPais());
		ntb.setGeoCampo1(vo.getGeoCampo1());
		ntb.setGeoCampo2(vo.getGeoCampo2());
		ntb.setContatosRegistrados(vo.getTotalContatosIdentificados());
		ntb.setIndLiberdade(vo.getPopulacaoPrivadaLiberdade());
		ntb.setIndProfSaude(vo.getProfissionalSaude());
		ntb.setIndSitRua(vo.getPopulacaoSituacaoRua());
		ntb.setIndImigrantes(vo.getImigrantes());
		ntb.setIndTabagismo(vo.getTabagismo());
		ntb.setIndAntiRetroviral(vo.getTerapiaAntirretroviral());
		ntb.setIndBeneficiario(vo.getBeneficiarioProgramaTransferenciaRendaGoverno());
		ntb.setIndTmr(vo.getTesteMolecularRapido());
		ntb.setIndSensibilidade(vo.getTesteSensibilidade());
		ntb.setMunicipioNotificacao(vo.getMunicipioNotificacao());
		ntb.setIndOutrasDoencas(vo.getIndOutrasDoencas());
		ntb.setDoenca(vo.getDoenca());
		
		AghCid cid = this.aghuFacade.obterCid(vo.getCodigoCid());
		ntb.setCid(cid);
		
		AghAtendimentos atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(vo.getAtdSeq());
		ntb.setAtendimento(atendimento);
		
		return ntb;
	}

	public SinamVO obterNotificacaoTuberculostatica(Integer ntbSeq) {
		SinamVO vo = new SinamVO();
		MpmNotificacaoTb ntb = this.mpmNotificacaoTbDAO.obterPorChavePrimaria(ntbSeq);

		vo.setSeq(ntbSeq);
		vo.setDataNotificacao(ntb.getDtNotificacao());
		// Removida conforme melhoria #50558
//		vo.setDataDiagnostico(ntb.getDtDiagnostico());
		
		if (ntb.getUf() != null) {
			vo.setUfResidencia(ntb.getUf().getSigla());
		}
		
		vo.setMunicipioNotificacao(vo.getMunicipioNotificacao());
		vo.setUnidadeSaude(ntb.getUnidadeDeSaude());
		vo.setCodigo(ntb.getCnes());
		vo.setGestante(ntb.getIndGestante());
		vo.setEscolaridade(ntb.getEscolaridade());

		vo.setTipoEntrada(ntb.getTipoEntrada());
		vo.setForma(ntb.getForma());
		vo.setAids(ntb.getIndAids());
		vo.setDiabetes(ntb.getIndDiabetes());
		vo.setDoencaMental(ntb.getIndDoencaMental());
		vo.setAlcoolismo(ntb.getIndAlcoolismo());
		vo.setUsoDrogasIlicitas(ntb.getIndDrogasIlicitas());

		vo.setPleural(converterDominiosExtrapulmonares(ntb.getIndPleural()));
		vo.setGangPerif(converterDominiosExtrapulmonares(ntb.getIndGangPerif()));
		vo.setGeniturinaria(converterDominiosExtrapulmonares(ntb.getIndGenitoUrinaria()));
		vo.setOssea(converterDominiosExtrapulmonares(ntb.getIndOssea()));
		vo.setOcular(converterDominiosExtrapulmonares(ntb.getIndOcular()));
		vo.setMiliar(converterDominiosExtrapulmonares(ntb.getIndMiliar()));
		vo.setMeningite(converterDominiosExtrapulmonares(ntb.getIndMeningite()));
		vo.setCutanea(converterDominiosExtrapulmonares(ntb.getIndCutanea()));
		vo.setLaringea(converterDominiosExtrapulmonares(ntb.getIndLaringea()));
		vo.setBaciloscopiaEscarro(ntb.getBaciloscopiaEscarro());
		vo.setRadiografiaTorax(ntb.getRaioxTorax());
		vo.setDescricaoAgravo(ntb.getDescOutroAgravo());
		vo.setCultura(ntb.getCulturaEscarro());
		vo.setHiv(ntb.getHiv());
		vo.setHistopatologia(ntb.getHistopatologia());
		// Removida conforme melhoria #50558
//		vo.setDataInicioTratamentoAtual(ntb.getDtInicioTratAtual());
		
		if (ntb.getProntuario() != null) {
			vo.setNroProntuario(CoreUtil.formataProntuario(ntb.getProntuario()));
		}
		
		vo.setNomePaciente(ntb.getNomePaciente());
		vo.setDataNascimento(ntb.getDtNascimento());
		vo.setIdade(ntb.getIdade());
		vo.setMediaIdade(ntb.getEspecIdade());
		vo.setSexo(ntb.getSexo());
		vo.setRaca(ntb.getRaca());
		vo.setNumeroCartaoSus(ntb.getNroCartaoSus());
		vo.setNomeMae(ntb.getNomeMae());
		vo.setLogradouro(ntb.getLogradouro());
		
		if (ntb.getNumeroLogradouro() != null) {
			vo.setNumero(Integer.valueOf(ntb.getNumeroLogradouro()));
		}
		
		vo.setCodigoLogradouro(ntb.getCodigoLogradouro());
		vo.setComplemento(ntb.getComplLogradouro());
		vo.setPontoReferencia(ntb.getPontoReferencia());
		vo.setMunicipioResidencia(ntb.getMunicipioResidencia());
		vo.setDistrito(ntb.getDistrito());
		vo.setBairro(ntb.getBairro());
		vo.setCep(ntb.getCep());
		vo.setDddTelefone(ntb.getDddTelefone());
		vo.setNumeroTelefone(ntb.getNumeroTelefone());
		vo.setZona(ntb.getZona());
		vo.setPais(ntb.getPais());
		vo.setGeoCampo1(ntb.getGeoCampo1());
		vo.setGeoCampo2(ntb.getGeoCampo2());
		vo.setTotalContatosIdentificados(ntb.getContatosRegistrados());
		vo.setPopulacaoPrivadaLiberdade(ntb.getIndLiberdade());
		vo.setProfissionalSaude(ntb.getIndProfSaude());
		vo.setPopulacaoSituacaoRua(ntb.getIndSitRua());
		vo.setImigrantes(ntb.getIndImigrantes());
		vo.setTabagismo(ntb.getIndTabagismo());
		vo.setTerapiaAntirretroviral(ntb.getIndAntiRetroviral());
		vo.setBeneficiarioProgramaTransferenciaRendaGoverno(ntb.getIndBeneficiario());
		vo.setTesteMolecularRapido(ntb.getIndTmr());
		vo.setTesteSensibilidade(ntb.getIndSensibilidade());
		vo.setCodigoCid(ntb.getCid() != null ? ntb.getCid().getCodigo() : null);
		vo.setOutraExtrapulmonar(ntb.getDescrOutraExtrapulmonar());
		vo.setIndOutrasDoencas(ntb.getIndOutrasDoencas());
		vo.setDoenca(ntb.getDoenca());
		
		if (ntb.getCidade() != null) {
			vo.setCodigoIbge(ntb.getCidade().getCodIbge());
		}
		
		vo.setAtdSeq(ntb.getAtendimento().getSeq());
		
		return vo;
	}
	
	private Boolean converterDominiosExtrapulmonares(final Object campo) {
		if (campo != null) {
			return true;
		}
		return false;
	}
	
	public void persistirNotificacaoTuberculostatico(SinamVO vo) throws ApplicationBusinessException {
		MpmNotificacaoTb notificacao = prepararObjeto(vo);
		
		notificacao.setIndConcluido(true);
		this.mpmNotificacaoTbRN.persistir(notificacao);
	}
}
