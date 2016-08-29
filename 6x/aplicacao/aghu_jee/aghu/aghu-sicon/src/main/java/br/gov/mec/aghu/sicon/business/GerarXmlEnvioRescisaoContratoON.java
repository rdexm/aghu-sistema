package br.gov.mec.aghu.sicon.business;

import java.io.StringWriter;
import java.io.Writer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.business.EnvioContratoSiasgSiconON.EnvioContratoSiasgSiconONExceptionCode;
import br.gov.mec.aghu.sicon.dao.ScoContratoDAO;
import br.gov.mec.aghu.sicon.util.Cnet;
import br.gov.mec.aghu.sicon.util.ObjectFactory;
import br.gov.mec.aghu.sicon.util.SiconUtil;
import br.gov.mec.aghu.sicon.vo.DadosEnvioVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@SuppressWarnings({"PMD.AtributoEmSeamContextManager"})
@Stateless
public class GerarXmlEnvioRescisaoContratoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GerarXmlEnvioRescisaoContratoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoContratoDAO scoContratoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8077267415051281020L;

	static final String AGHU_SICON = "aghuSicon";

	private Writer xmlEnvio;

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public DadosEnvioVO gerarXml(Integer seqContrato, String autenticacaoSicon)
			throws ApplicationBusinessException {

		DadosEnvioVO dadosEnvioVO = new DadosEnvioVO();
		Marshaller marshaller = null;

		try {
			JAXBContext context = JAXBContext
					.newInstance("br.gov.mec.aghu.sicon.util");
			marshaller = context.createMarshaller();

		} catch (JAXBException e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(

			EnvioContratoSiasgSiconONExceptionCode.ERRO_CONTEXTO_XML);
		}
		
		ScoContrato contrato = getScoContratoDAO().obterPorChavePrimaria(
				seqContrato);

		if (contrato == null) {
			throw new ApplicationBusinessException(
					EnvioContratoSiasgSiconONExceptionCode.CONTRATO_NAO_ENCONTRADO);
		}

		Cnet cnet = new ObjectFactory().createCnet();

		AghParametros ambiente = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AMB_INTEGRACAO_SICON);

		// Tags XML

		cnet.setAmbiente(ambiente.getVlrTexto().toLowerCase());

		cnet.setSistema(AGHU_SICON);

		cnet.setAcao("rescisão");

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
		
		Long cpf = servidorLogado.getPessoaFisica().getCpf();
		
		if(cpf != null){
			String strCpf = cpf.toString();
			strCpf = StringUtils.leftPad(strCpf, 11, "0");
			cnet.setCpf(strCpf);
		
		}else{
			throw new ApplicationBusinessException(
					EnvioContratoSiasgSiconONExceptionCode.CPF_NAO_ENCONTRADO);
		}
		

		cnet.setSenha(autenticacaoSicon);

		cnet.setUasg(getAghuFacade().getUasg());
		
		if (contrato.getCodInternoUasg() == null
				|| StringUtils.isBlank(contrato.getCodInternoUasg())) {

			String codInterno = contrato.getNrContrato().toString()
					+ SiconUtil.obtemAnoString(contrato.getDtAssinatura());

			cnet.setCodInternoUasg(new ObjectFactory()
					.createCnetCodInternoUasg(codInterno));

		} else {
			cnet.setCodInternoUasg(new ObjectFactory()
					.createCnetCodInternoUasg(contrato.getCodInternoUasg()));
		}
		
		cnet.setTipo(contrato.getTipoContratoSicon().obtemCodigoSicon());
		
		cnet.setNumero(contrato.getNrContrato().intValue());
		
		if (contrato.getDtAssinatura() != null) {
			cnet.setAno(SiconUtil.obtemAnoXGC(contrato.getDtAssinatura()));
		}
		
		// Tags preenchidas em branco porém são obrigatórias para envio
		// O doc. 'MANUAL - xml_sicon_contrato_entidade_20040205' diz que são opcionais,
		// mas o sistema Sicon recusa arquivos que não tenham as tags
		cnet.setTipoContratoOriginal(new ObjectFactory().createCnetTipoContratoOriginal(""));
		cnet.setNumeroContratoOriginal(new ObjectFactory().createCnetNumeroContratoOriginal(""));
		cnet.setAnoContratoOriginal(new ObjectFactory().createCnetAnoContratoOriginal(""));
		cnet.setUasgLicit(new ObjectFactory().createCnetUasgLicit(""));
		cnet.setModalidadeLicit(new ObjectFactory().createCnetModalidadeLicit(""));
		cnet.setNumeroLicit(new ObjectFactory().createCnetNumeroLicit(""));
		cnet.setAnoLicit(new ObjectFactory().createCnetAnoLicit(""));
		cnet.setInciso(new ObjectFactory().createCnetInciso(""));
		cnet.setDtPublicacao(new ObjectFactory().createCnetDtPublicacao(""));
		cnet.setAquisicao(new ObjectFactory().createCnetAquisicao(""));
		cnet.setObjeto(new ObjectFactory().createCnetObjeto(""));
		cnet.setCnpjcpfContratado(new ObjectFactory().createCnetCnpjcpfContratado(""));
		cnet.setRazaoSocialContratado(new ObjectFactory().createCnetRazaoSocialContratado("''")); // ''
		cnet.setCnpjContratante(new ObjectFactory().createCnetCnpjContratante(""));
		cnet.setRazaoSocialContratante(new ObjectFactory().createCnetRazaoSocialContratante("''")); //''
		cnet.setProcesso(new ObjectFactory().createCnetProcesso(""));
		cnet.setFundamentoLegal(new ObjectFactory().createCnetFundamentoLegal(""));
		cnet.setDtInicioVigencia(new ObjectFactory().createCnetDtInicioVigencia(""));
		cnet.setDtFimVigencia(new ObjectFactory().createCnetDtFimVigencia(""));
		cnet.setDtAssinatura(new ObjectFactory().createCnetDtAssinatura(""));
		cnet.setNuOrdemServico(new ObjectFactory().createCnetNuOrdemServico(""));
		cnet.setValorTotal(new ObjectFactory().createCnetValorTotal(""));
		cnet.setTipoAditivo(new ObjectFactory().createCnetTipoAditivo(""));
		cnet.setDadosOrcamentarios(new ObjectFactory().createCnetDadosOrcamentarios());
		cnet.setItens(new ObjectFactory().createCnetItens());
		cnet.setTipoEmergencial("");
		
		xmlEnvio = new StringWriter();

		try {
			marshaller.marshal(cnet, xmlEnvio);

		} catch (JAXBException e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(
					EnvioContratoSiasgSiconONExceptionCode.ERRO_CRIACAO_XML);
		}

		dadosEnvioVO.setXmlEnvio(xmlEnvio.toString());
		dadosEnvioVO.setCnet(cnet);
		
		return dadosEnvioVO;
	}

	protected ScoContratoDAO getScoContratoDAO() {
		return scoContratoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
