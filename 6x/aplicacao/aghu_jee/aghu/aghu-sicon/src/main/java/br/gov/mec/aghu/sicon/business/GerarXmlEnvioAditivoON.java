package br.gov.mec.aghu.sicon.business;

import java.io.StringWriter;
import java.io.Writer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoAditivo;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.business.EnvioContratoSiasgSiconON.EnvioContratoSiasgSiconONExceptionCode;
import br.gov.mec.aghu.sicon.util.Cnet;
import br.gov.mec.aghu.sicon.util.ObjectFactory;
import br.gov.mec.aghu.sicon.util.SiconUtil;
import br.gov.mec.aghu.sicon.vo.DadosEnvioVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.CyclomaticComplexity","PMD.AtributoEmSeamContextManager"})
@Stateless
public class GerarXmlEnvioAditivoON extends BaseBusiness {

	private static final String DD_M_MYYYY = "ddMMyyyy";

	private static final Log LOG = LogFactory.getLog(GerarXmlEnvioAditivoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	public enum GerarXmlEnvioAditivoONExceptionCode implements
			BusinessExceptionCode {
		ERRO_CONTEXTO_XML, ADITIVO_NAO_ENCONTRADO
	}
	
	private static final long serialVersionUID = 6134595143324075006L;
	static final String AGHU_SICON = "aghuSicon";
	static final String INCLUSAO = "inclusão";
	static final String RESCISAO = "rescisão";
	private Writer xmlEnvio;

	
	public DadosEnvioVO gerarXml(ScoAditContrato aditContrato, String autenticacaoSicon)
			throws ApplicationBusinessException {
		
		if (aditContrato == null) {
			throw new ApplicationBusinessException(
					GerarXmlEnvioAditivoONExceptionCode.ADITIVO_NAO_ENCONTRADO);
		}
		
		if (aditContrato.getDataRescicao() != null){
			return this.gerarXmlRescisaoAditivo(aditContrato, autenticacaoSicon);
		}else{
			return this.gerarXmlAditivo(aditContrato, autenticacaoSicon);
		}
	}
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private DadosEnvioVO gerarXmlAditivo(ScoAditContrato aditContrato, String autenticacaoSicon)
			throws ApplicationBusinessException {

		DadosEnvioVO dadosEnvioVO = new DadosEnvioVO();
		Marshaller marshaller = null;

		try {
			JAXBContext context = JAXBContext
					.newInstance("br.gov.mec.aghu.sicon.util");
			marshaller = context.createMarshaller();
			
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		} catch (JAXBException e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(

			GerarXmlEnvioAditivoONExceptionCode.ERRO_CONTEXTO_XML);
		}

		Cnet cnet = new ObjectFactory().createCnet();

		AghParametros url = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AMB_INTEGRACAO_SICON);

		cnet.setAmbiente(url.getVlrTexto().toLowerCase());

		cnet.setSistema(AGHU_SICON);
		
		cnet.setAcao(INCLUSAO);

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
		
		cnet.setUasgSubrog(new ObjectFactory().createCnetUasgSubrog(""));
				
		if (aditContrato.getCont().getCodInternoUasg() == null
				|| StringUtils.isBlank(aditContrato.getCont().getCodInternoUasg())) {
		
			String codInterno = aditContrato.getCont().getNrContrato().toString()
							  + SiconUtil.obtemAnoString(aditContrato.getCont().getDtAssinatura());
			
			cnet.setCodInternoUasg(new ObjectFactory()
					.createCnetCodInternoUasg(codInterno));
		
		}else{
			cnet.setCodInternoUasg(new ObjectFactory()
					.createCnetCodInternoUasg(aditContrato.getCont().getCodInternoUasg()));
		}
		
		if (aditContrato.getTipoContratoSicon() != null
				&& aditContrato.getTipoContratoSicon().getCodigoSicon() != null) {
			cnet.setTipo(aditContrato.getTipoContratoSicon().getCodigoSicon()
					.toString());
		}

		if (aditContrato.getId() != null
				&& aditContrato.getId().getSeq() != null) {
			cnet.setNumero(aditContrato.getId().getSeq());
		}

		if (aditContrato.getDataAssinatura() != null) {
			cnet.setAno(SiconUtil.obtemAnoXGC(aditContrato.getDataAssinatura()));
		}

		if (aditContrato.getCont() != null
				&& aditContrato.getCont().getTipoContratoSicon() != null
				&& aditContrato.getCont().getTipoContratoSicon()
						.getCodigoSicon() != null) {

			cnet.setTipoContratoOriginal(new ObjectFactory()
					.createCnetTipoContratoOriginal(aditContrato.getCont()
							.getTipoContratoSicon().getCodigoSicon().toString()));
		}

		if (aditContrato.getCont() != null
				&& aditContrato.getCont().getNrContrato() != null) {
			
			cnet.setNumeroContratoOriginal(new ObjectFactory()
					.createCnetNumeroContratoOriginal(aditContrato.getCont()
							.getNrContrato().toString()));
		}

		if (aditContrato.getCont() != null
				&& aditContrato.getCont().getDtAssinatura() != null) {

			cnet.setAnoContratoOriginal(new ObjectFactory()
					.createCnetAnoContratoOriginal(SiconUtil.obtemAnoString(
							aditContrato.getCont().getDtAssinatura())
							.toString()));
		}
		
		cnet.setUasgLicit(new ObjectFactory().createCnetUasgLicit(""));
		cnet.setModalidadeLicit(new ObjectFactory().createCnetModalidadeLicit(""));		
		cnet.setNumeroLicit(new ObjectFactory().createCnetNumeroLicit(""));		
		cnet.setAnoLicit(new ObjectFactory().createCnetAnoLicit(""));
		cnet.setInciso(new ObjectFactory().createCnetInciso(""));

		if (aditContrato.getDataPublicacao() != null) {
			cnet.setDtPublicacao(new ObjectFactory()
					.createCnetDtPublicacao(DateUtil.obterDataFormatada(
							aditContrato.getDataPublicacao(), DD_M_MYYYY)));
		}else{
			cnet.setDtPublicacao(new ObjectFactory().createCnetDtPublicacao(""));
		}
		
		cnet.setAquisicao(new ObjectFactory().createCnetAquisicao(""));

		if (aditContrato.getObjetoContrato() != null) {
			cnet.setObjeto(new ObjectFactory()
					.createCnetObjeto(aditContrato.getObjetoContrato()
							.toString()));
		}

		cnet.setCnpjcpfContratado(new ObjectFactory().createCnetCnpjcpfContratado(""));
		
		if (aditContrato.getCont() != null &&
			aditContrato.getCont().getFornecedor() != null &&
			aditContrato.getCont().getFornecedor().getRazaoSocial() != null) {
			
			cnet.setRazaoSocialContratado(new ObjectFactory()
					.createCnetRazaoSocialContratado(aditContrato.getCont().getFornecedor()
							.getRazaoSocial().toString()));
		}
		
		cnet.setCnpjContratante(new ObjectFactory().createCnetCnpjContratante(""));
		
		cnet.setProcesso(new ObjectFactory().createCnetProcesso(""));
		
		if (aditContrato.getCont().getFundamentoLegal() != null) {
			cnet.setFundamentoLegal(new ObjectFactory()
					.createCnetFundamentoLegal(aditContrato.getCont()
							.getFundamentoLegal().toString()));
		}
		
		if (aditContrato.getDtInicioVigencia() != null) {
			cnet.setDtInicioVigencia(new ObjectFactory()
					.createCnetDtInicioVigencia(DateUtil
							.obterDataFormatada(
									aditContrato.getDtInicioVigencia(),
									DD_M_MYYYY)));
		}
		
		if (aditContrato.getDtFimVigencia() != null) {
			cnet.setDtFimVigencia(new ObjectFactory()
					.createCnetDtFimVigencia(DateUtil.obterDataFormatada(
							aditContrato.getDtFimVigencia(), DD_M_MYYYY)));
		}
		
		if (aditContrato.getDataAssinatura() != null) {
			cnet.setDtAssinatura(new ObjectFactory()
					.createCnetDtAssinatura(DateUtil.obterDataFormatada(
							aditContrato.getDataAssinatura(), DD_M_MYYYY)));
		}
		
		cnet.setNuOrdemServico(new ObjectFactory().createCnetNuOrdemServico(""));
		
		if (aditContrato.getVlAditivado() != null) {
			
			String valorFormatado = AghuNumberFormat.formatarValor(
					aditContrato.getVlAditivado(), "#.00");
			
			valorFormatado = removePontoVirgula(valorFormatado);
			
			cnet.setValorTotal(new ObjectFactory()
					.createCnetValorTotal(valorFormatado));
		}
		
		if (aditContrato.getIndTipoAditivo() != null) {
			
			String tpAditivo = "";
			
			if (aditContrato.getIndTipoAditivo() == DominioTipoAditivo.A){
				tpAditivo = "+";
			}else{
				tpAditivo = "-";
			}
			
			cnet.setTipoAditivo(new ObjectFactory()
					.createCnetTipoAditivo(tpAditivo));
		}
		
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
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private DadosEnvioVO gerarXmlRescisaoAditivo(ScoAditContrato aditContrato, String autenticacaoSicon)
			throws ApplicationBusinessException {

		DadosEnvioVO dadosEnvioVO = new DadosEnvioVO();
		Marshaller marshaller = null;

		try {
			JAXBContext context = JAXBContext
					.newInstance("br.gov.mec.aghu.sicon.util");
			marshaller = context.createMarshaller();
			
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		} catch (JAXBException e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(

			GerarXmlEnvioAditivoONExceptionCode.ERRO_CONTEXTO_XML);
		}

		if (aditContrato == null) {
			throw new ApplicationBusinessException(
					GerarXmlEnvioAditivoONExceptionCode.ADITIVO_NAO_ENCONTRADO);
		}

		Cnet cnet = new ObjectFactory().createCnet();

		AghParametros url = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AMB_INTEGRACAO_SICON);

		cnet.setAmbiente(url.getVlrTexto().toLowerCase());

		cnet.setSistema(AGHU_SICON);
		
		cnet.setAcao(RESCISAO);

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
		
		cnet.setUasgSubrog(new ObjectFactory().createCnetUasgSubrog(""));
				
		if (aditContrato.getCont().getCodInternoUasg() == null
				|| StringUtils.isBlank(aditContrato.getCont().getCodInternoUasg())) {
		
			String codInterno = aditContrato.getCont().getNrContrato().toString()
							  + SiconUtil.obtemAnoString(aditContrato.getCont().getDtAssinatura());
			
			cnet.setCodInternoUasg(new ObjectFactory()
					.createCnetCodInternoUasg(codInterno));
		
		}else{
			cnet.setCodInternoUasg(new ObjectFactory()
					.createCnetCodInternoUasg(aditContrato.getCont().getCodInternoUasg()));
		}
		
		if (aditContrato.getTipoContratoSicon() != null
				&& aditContrato.getTipoContratoSicon().getCodigoSicon() != null) {
			cnet.setTipo(aditContrato.getTipoContratoSicon().getCodigoSicon()
					.toString());
		}

		if (aditContrato.getId() != null
				&& aditContrato.getId().getSeq() != null) {
			cnet.setNumero(aditContrato.getId().getSeq());
		}

		if (aditContrato.getDataAssinatura() != null) {
			cnet.setAno(SiconUtil.obtemAnoXGC(aditContrato.getDataAssinatura()));
		}

		if (aditContrato.getCont() != null
				&& aditContrato.getCont().getTipoContratoSicon() != null
				&& aditContrato.getCont().getTipoContratoSicon()
						.getCodigoSicon() != null) {

			cnet.setTipoContratoOriginal(new ObjectFactory()
					.createCnetTipoContratoOriginal(aditContrato.getCont()
							.getTipoContratoSicon().getCodigoSicon().toString()));
		}

		if (aditContrato.getCont() != null
				&& aditContrato.getCont().getNrContrato() != null) {
			
			cnet.setNumeroContratoOriginal(new ObjectFactory()
					.createCnetNumeroContratoOriginal(aditContrato.getCont()
							.getNrContrato().toString()));
		}

		if (aditContrato.getCont() != null
				&& aditContrato.getCont().getDtAssinatura() != null) {

			cnet.setAnoContratoOriginal(new ObjectFactory()
					.createCnetAnoContratoOriginal(SiconUtil.obtemAnoString(
							aditContrato.getCont().getDtAssinatura())
							.toString()));
		}
		
		cnet.setUasgLicit(new ObjectFactory().createCnetUasgLicit(""));
		cnet.setModalidadeLicit(new ObjectFactory().createCnetModalidadeLicit(""));		
		cnet.setNumeroLicit(new ObjectFactory().createCnetNumeroLicit(""));		
		cnet.setAnoLicit(new ObjectFactory().createCnetAnoLicit(""));
		cnet.setInciso(new ObjectFactory().createCnetInciso(""));
		cnet.setDtPublicacao(new ObjectFactory().createCnetDtPublicacao(""));
		cnet.setAquisicao(new ObjectFactory().createCnetAquisicao(""));
		cnet.setObjeto(new ObjectFactory().createCnetObjeto(""));
		cnet.setCnpjcpfContratado(new ObjectFactory().createCnetCnpjcpfContratado(""));
		
		if (aditContrato.getCont() != null &&
			aditContrato.getCont().getFornecedor() != null &&
			aditContrato.getCont().getFornecedor().getRazaoSocial() != null) {
			
			cnet.setRazaoSocialContratado(new ObjectFactory()
					.createCnetRazaoSocialContratado(aditContrato.getCont().getFornecedor()
							.getRazaoSocial().toString()));
		}
		
		cnet.setCnpjContratante(new ObjectFactory().createCnetCnpjContratante(""));
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
	
	
	
	
	/**
	 * Retira caracteres de ponto ou vírgula e retorna uma string formatada
	 * Exemplo: String valor = 100.10 , retornará String formatada = 10010
	 * 
	 */
	private String removePontoVirgula(String valor){
		
		String valorFormatado = valor;
		
		if(valorFormatado != null){
			valorFormatado = valorFormatado.replace(".", "");
			valorFormatado = valorFormatado.replace(",", "");
		}
		
		return valorFormatado;
	}

	public Writer getXmlEnvio() {
		return xmlEnvio;
	}

	public void setXmlEnvio(Writer xmlEnvio) {
		this.xmlEnvio = xmlEnvio;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		//return IparametroFacade;
		return this.parametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
