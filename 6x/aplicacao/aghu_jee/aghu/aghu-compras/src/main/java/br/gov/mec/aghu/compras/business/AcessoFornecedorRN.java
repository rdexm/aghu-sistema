package br.gov.mec.aghu.compras.business;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornecedorPedidoDAO;
import br.gov.mec.aghu.compras.dao.ScoContatoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoProgrCodAcessoFornDAO;
import br.gov.mec.aghu.compras.vo.ScoProgrCodAcessoFornVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoProgrCodAcessoForn;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.mail.AnexoEmail;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class AcessoFornecedorRN extends BaseBusiness {

	
	private static final String E70303 = "#E70303";

	private static final long serialVersionUID = -8819518903346077958L;
	
	private static final Log LOG = LogFactory.getLog(AcessoFornecedorRN.class);

	@Inject
	private ScoAutorizacaoFornecedorPedidoDAO scoAutorizacaoFornecedorPedidoDAO;
	
	@Inject
	private ScoProgrCodAcessoFornDAO scoProgrCodAcessoFornDAO;
	
	@Inject
	private ScoContatoFornecedorDAO scoContatoFornecedorDAO;
	
	@Inject
	private EmailUtil emailUtil;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	
	public enum AcessoFornecedorRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CODIGO_CADASTRADO, MENSAGEM_NENHUM_CONTATO_ENCONTRADO, MENSAGEM_SENHA_ACESSO_CADASTRADA_FORNECEDOR;
	}
	
	// RN 03
	public void persistirAcessoFornecedor(ScoProgrCodAcessoForn acessoFornecedor) throws ApplicationBusinessException {
		
		if(scoProgrCodAcessoFornDAO.existeAcessoFornecedorPorFornecedor(acessoFornecedor.getScoFornecedor())) {
			throw new ApplicationBusinessException(AcessoFornecedorRNExceptionCode.MENSAGEM_SENHA_ACESSO_CADASTRADA_FORNECEDOR);
		}
		
		validarCodigoAcesso(acessoFornecedor);
		acessoFornecedor.setRapServidoresByScoCafSerFk1(this.getServidorLogadoFacade().obterServidorLogado());
		acessoFornecedor.setDtGeracao(new Date());
		
		scoProgrCodAcessoFornDAO.persistir(acessoFornecedor);
		scoProgrCodAcessoFornDAO.flush();
	}

	private void validarCodigoAcesso(ScoProgrCodAcessoForn acessoFornecedor)
			throws ApplicationBusinessException {
		if (StringUtils.isEmpty(acessoFornecedor.getCodAcesso())) {
			// RN 04
			String codigoAcesso = acessoFornecedor.getScoFornecedor().getRazaoSocial().trim().substring(0,4) + StringUtils.leftPad(String.valueOf(acessoFornecedor.getScoFornecedor().getNumero()), 6, '0');
			acessoFornecedor.setCodAcesso(codigoAcesso);
		} else {
			String codigoBanco = scoProgrCodAcessoFornDAO.consultarCodigoAcesso(acessoFornecedor.getCodAcesso(), acessoFornecedor.getScoFornecedor().getNumero());
			
			if (!StringUtils.isEmpty(codigoBanco)) {
				throw new ApplicationBusinessException(AcessoFornecedorRNExceptionCode.MENSAGEM_CODIGO_CADASTRADO);
			}
		}
	}
	
	public void atualizarAcessoFornecedor(ScoProgrCodAcessoForn acessoFornecedor) throws ApplicationBusinessException {
		validarCodigoAcesso(acessoFornecedor);
		
		acessoFornecedor.setDtAlteracao(new Date());
		acessoFornecedor.setRapServidoresByScoCafSerAlteracaoFk1(this.getServidorLogadoFacade().obterServidorLogado());
		
		scoProgrCodAcessoFornDAO.atualizar(acessoFornecedor);
		scoProgrCodAcessoFornDAO.flush();
	}

	public List<ScoProgrCodAcessoFornVO> listarFornecedores(ScoFornecedor fornecedor, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		// TODO Auto-generated method stub
		List<ScoProgrCodAcessoForn> listaResultado = scoProgrCodAcessoFornDAO.listarFornecedores(fornecedor, firstResult, maxResult, orderProperty, asc);
		List<ScoProgrCodAcessoFornVO> retorno = new ArrayList<ScoProgrCodAcessoFornVO>();
		for (ScoProgrCodAcessoForn acessoFornecedor : listaResultado) {
			
			ScoProgrCodAcessoFornVO vo = new ScoProgrCodAcessoFornVO();
			
			vo.setSeq(acessoFornecedor.getSeq());
			vo.setScoFornecedor(acessoFornecedor.getScoFornecedor());
			vo.setDtGeracao(acessoFornecedor.getDtGeracao());
			vo.setDtEnvioSenha(acessoFornecedor.getDtEnvioFornecedor());// TODO: Verificar a coluna.
			vo.setDtEnvioContato(acessoFornecedor.getDtEnvioContato());
			vo.setCodAcesso(acessoFornecedor.getCodAcesso());
			
			Long pendencias = scoAutorizacaoFornecedorPedidoDAO.verificarPendenciasFornecedor(acessoFornecedor.getScoFornecedor().getNumero());
			
			if (pendencias == 0 && acessoFornecedor.getDtGeracao() == null) {
				vo.setPendencia("Fornecedor sem AFP Publicada");
				retorno.add(vo);
				continue;
			} else if (pendencias > 0 && acessoFornecedor.getDtGeracao() == null) {
				vo.setPendencia("Criar Senha para o Fornecedor");
				vo.setColor(E70303);
				retorno.add(vo);
				continue;
			} else if (acessoFornecedor.getDtGeracao() != null && acessoFornecedor.getDtEnvioContato() == null) {
				vo.setPendencia("Enviar Contatos ao Fornecedor");
				vo.setColor(E70303);
				retorno.add(vo);
				continue;
			} else {
				Long contatos = scoContatoFornecedorDAO.verificarContatosFornecedor(acessoFornecedor.getScoFornecedor().getNumero(), acessoFornecedor.getDtEnvioContato());
				
				if (contatos == 0 && acessoFornecedor.getDtGeracao() != null && acessoFornecedor.getDtEnvioContato() != null) {
					vo.setPendencia("Atualizar Contatos Fornecedor (Clicar em Senha Internet)");
					vo.setColor(E70303);
					retorno.add(vo);
					continue;
				} else if (acessoFornecedor.getDtGeracao() != null && acessoFornecedor.getDtEnvioContato() != null && acessoFornecedor.getDtEnvioFornecedor() == null) {
					vo.setPendencia("Enviar Senha ao Fornecedor");
					vo.setColor(E70303);
					retorno.add(vo);
					continue;
				} else if (acessoFornecedor.getDtEnvioFornecedor() != null){
					vo.setPendencia("Sem Pendência - Senha já enviada ao Fornecedor");
					vo.setColor("#37C400");
					retorno.add(vo);
					continue;
				}
			}
			
		}
		
		return retorno;
	}
	


	public String buscarPendenciaAcessoFornecedor(ScoProgrCodAcessoForn acessoFornecedor) {
		
		Long pendencias = scoAutorizacaoFornecedorPedidoDAO.verificarPendenciasFornecedor(acessoFornecedor.getScoFornecedor().getNumero());
		
		if (pendencias == 0 && acessoFornecedor.getDtGeracao() == null) {
			return "Fornecedor sem AFP Publicada";
		} else if (pendencias > 0 && acessoFornecedor.getDtGeracao() == null) {
			return "Criar Senha para o Fornecedor";
		} else if (acessoFornecedor.getDtGeracao() != null && acessoFornecedor.getDtEnvioContato() == null) {
			return "Enviar Contatos ao Fornecedor";
		} else {
			Long contatos = scoContatoFornecedorDAO.verificarContatosFornecedor(acessoFornecedor.getScoFornecedor().getNumero(), acessoFornecedor.getDtEnvioContato());
			
			if (contatos == 0 && acessoFornecedor.getDtGeracao() != null && acessoFornecedor.getDtEnvioContato() != null) {
				return "Atualizar Contatos Fornecedor (Clicar em Senha Internet)";
			} else if (acessoFornecedor.getDtGeracao() != null && acessoFornecedor.getDtEnvioContato() != null && acessoFornecedor.getDtEnvioFornecedor() == null) {
				return "Enviar Senha ao Fornecedor";
			} else if (acessoFornecedor.getDtEnvioFornecedor() != null){
				return "Sem Pendência - Senha já enviada ao Fornecedor";
			}
		}
		
		return null;
	}

	public void enviarEmailContatos(Integer seq) throws ApplicationBusinessException {
		ScoProgrCodAcessoForn acessoFornecedor = scoProgrCodAcessoFornDAO.obterPorChavePrimaria(seq);
		
		//C6
		List<ScoContatoFornecedor> listaContatos = scoContatoFornecedorDAO.obterContatosParaReceberEmailFornecedor(acessoFornecedor.getScoFornecedor());
		
		if (listaContatos == null || listaContatos.isEmpty()) {
			throw new ApplicationBusinessException(AcessoFornecedorRNExceptionCode.MENSAGEM_NENHUM_CONTATO_ENCONTRADO);
		}
		
		AghParametros paramHU = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PARAMETRO_HU);
		AghParametros paramRazaoSocial = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		AghParametros paramMatriculaChefeCompras= parametroFacade.buscarAghParametro(AghuParametrosEnum.P_MATR_CHEFE_CPRAS);
		//C7
		short vinculo = 1;
		String nomeChefeCompras = registroColaboradorFacade.obterChefeComprasPorMatriculaVinculo(paramMatriculaChefeCompras.getVlrNumerico().intValue(), vinculo);
		String remetente = this.getServidorLogadoFacade().obterServidorLogado().getEmail();
		String assunto = "Contatos aptos a receber e-mail do " + paramHU.getVlrTexto();
		StringBuilder contatos = montarEmailContatosEnvio(listaContatos);
		String conteudo = super.getResourceBundleValue("CONTEUDO_EMAIL_ENVIO_CONTATOS_FORNECEDOR");
		String email = MessageFormat.format(conteudo, paramRazaoSocial.getVlrTexto(), paramHU.getVlrTexto(), contatos.toString(), nomeChefeCompras, paramHU.getVlrTexto());
		
		for (ScoContatoFornecedor contato : listaContatos) {
			emailUtil.enviaEmail(remetente, contato.getEMail(), null, assunto, email);
		}
		
		acessoFornecedor.setDtEnvioContato(new Date());
		scoProgrCodAcessoFornDAO.atualizar(acessoFornecedor);
		scoProgrCodAcessoFornDAO.flush();
	}
	
	private StringBuilder montarEmailContatosEnvio(List<ScoContatoFornecedor> listaContatos) {
		StringBuilder contatos = new StringBuilder(39);
		for (ScoContatoFornecedor contato : listaContatos) {
			contatos.append("<tr><td>" + contato.getNome() + "</td><td>" + "(" + contato.getDdd() + ") " + contato.getFone() + "</td><td>" + contato.getEMail() + "</td></tr>");
		}
		return contatos;
	}


	public void enviarEmailSenha(Integer seq, byte[] jasper) throws ApplicationBusinessException {
		ScoProgrCodAcessoForn acessoFornecedor = scoProgrCodAcessoFornDAO.obterPorChavePrimaria(seq);
		
		List<ScoContatoFornecedor> listaContatos = scoContatoFornecedorDAO.obterContatosParaReceberEmailFornecedor(acessoFornecedor.getScoFornecedor());
		
		if (listaContatos == null || listaContatos.isEmpty()) {
			throw new ApplicationBusinessException(AcessoFornecedorRNExceptionCode.MENSAGEM_NENHUM_CONTATO_ENCONTRADO);
		}
		
		AghParametros paramHU = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PARAMETRO_HU);
		AghParametros paramRazaoSocial = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		AghParametros paramMatriculaChefeCompras= parametroFacade.buscarAghParametro(AghuParametrosEnum.P_MATR_CHEFE_CPRAS);
		AghParametros paramContForn = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_LOCAL_CONTATO_FORNE);
		//C7
		short vinculo = 1;
		String nomeChefeCompras = registroColaboradorFacade.obterChefeComprasPorMatriculaVinculo(paramMatriculaChefeCompras.getVlrNumerico().intValue(), vinculo);
		String remetente = this.getServidorLogadoFacade().obterServidorLogado().getEmail();
		String assunto = "Senha de acesso ao Portal do " + paramHU.getVlrTexto();
		String conteudo = super.getResourceBundleValue("CONTEUDO_EMAIL_ENVIO_SENHA_FORNECEDOR");
		String email = MessageFormat.format(conteudo, paramRazaoSocial.getVlrTexto(), paramHU.getVlrTexto(), paramContForn.getVlrTexto(), nomeChefeCompras);
		
		
		// [ P_AGHU_PARAMETRO_HU]_[CGC_Fornecedor][ to_char(data atual, 'SSDDMIMMHH24')]
		String nomeAnexo = paramHU.getVlrTexto() + "_" + acessoFornecedor.getScoFornecedor().getCgc() + "_" + DateUtil.dataToString(new Date(), "ssddmmMMHH24");
		AnexoEmail anexoEmail = new AnexoEmail(nomeAnexo, jasper, "application/pdf");
		
		for (ScoContatoFornecedor contato : listaContatos) {
			emailUtil.enviaEmail(remetente, contato.getEMail(), null, assunto, email, anexoEmail);
		}
		
		acessoFornecedor.setDtEnvioFornecedor(new Date());
		scoProgrCodAcessoFornDAO.atualizar(acessoFornecedor);
		scoProgrCodAcessoFornDAO.flush();
	}

	public String buscarSituacaoAcessoFornecedor(ScoProgrCodAcessoForn acessoFornecedor, String colorSituacao) {
		Long pendencias = scoAutorizacaoFornecedorPedidoDAO.verificarPendenciasFornecedor(acessoFornecedor.getScoFornecedor().getNumero());
		
		if (pendencias == 0) {
			colorSituacao = E70303; 
			return "Fornecedor sem AFP Publicada";
		}
		
		if (acessoFornecedor.getCodAcesso() == null) {
			return "Criar Senha para o Fornecedor";
		}
		
		return "Senha já criada";
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}
