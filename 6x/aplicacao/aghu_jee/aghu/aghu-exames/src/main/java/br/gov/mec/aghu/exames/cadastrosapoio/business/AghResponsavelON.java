package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dominio.DominioCpfCgcResponsavel;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AghResponsavelDAO;
import br.gov.mec.aghu.exames.dao.AghResponsavelJnDAO;
import br.gov.mec.aghu.exames.vo.ClienteNfeVO;
import br.gov.mec.aghu.exames.vo.ResponsavelVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghPaisBcb;
import br.gov.mec.aghu.model.AghResponsavel;
import br.gov.mec.aghu.model.AghResponsavelJn;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AipBairrosCepLogradouroId;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAipCeps;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.senior.ISeniorService;

@Stateless
public class AghResponsavelON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AghResponsavelON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Resource
	private SessionContext ctx;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
		
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private ISeniorService seniorService;
	
	@Inject
	private AghResponsavelDAO	aghResponsavelDAO;
	
	@Inject
	private AghResponsavelJnDAO aghResponsavelJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8058855735355590734L;

	public enum ResponsavelRNExceptionCode implements BusinessExceptionCode {
		
		CPF_INVALIDO_RESPONSAVEL, // CPF Inválido
		CNPJ_INVALIDO_RESPONSAVEL, // CNPJ Invalido
		DOCUMENTO_DUPLICADO_RESPONSAVEL;
		
		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
			
	/**
	 * Tenta enviar cliente da NFe.<br>
	 * Caso ocorra algum erro na aborata o fluxo de execucao atual.
	 * 
	 * @param responsavel
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Long enviarCliente(AghResponsavel responsavel) {
		Long returnValue;
		
		try {
			returnValue = doEnviarCliente(responsavel);
		} catch (Exception e) {
			LOG.warn(e.getMessage(), e);
			returnValue = null;
		}
		
		return returnValue; 
	}
		
	/**
	 * Integracao com Nota Fiscal Eletronica
	 * @param responsavel
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Long doEnviarCliente(AghResponsavel responsavel) throws ApplicationBusinessException {
		ClienteNfeVO clienteNfeVo = new ClienteNfeVO();		
		VAipCeps cepCadastrado;
		AipCidades cidadeEnderecoCadastrado;
		Integer ctaRed = this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_NFSE_CTA_CONT_REDUZIDA);
		AghPaisBcb paisBrasil = this.examesFacade.obterAghPaisBcb(this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_SEQ_PAIS_BRASIL));
		
		clienteNfeVo.setCtaRed(ctaRed);
		
		if (responsavel.getAipPaciente()!= null) {
			AipPacientes paciente = pacienteFacade.obterPaciente(responsavel.getAipPaciente().getCodigo());
						
			clienteNfeVo.setApeCli(paciente.getNome());
			clienteNfeVo.setNomCli(paciente.getNome());
			
			if(paciente.getCpf() != null){
			   clienteNfeVo.setDocumento(paciente.getCpf().toString());
			}
			
			clienteNfeVo.setIntNet(responsavel.getEmail());
			clienteNfeVo.setEmaNfe(responsavel.getEmail());
			clienteNfeVo.setTipCli("F");
			clienteNfeVo.setTipMer("I");
			
			if (paisBrasil != null){
			    clienteNfeVo.setCodPai(paisBrasil.getCodigoBcb().toString());
			}
			
			
			AipEnderecosPacientes enderecoPadraoPaciente = this.cadastroPacienteFacade.obterEnderecoResidencialPadraoPaciente(paciente);
			
			if(enderecoPadraoPaciente.getCepEndereco() != null){
			   clienteNfeVo.setCepCli(enderecoPadraoPaciente.getCepEndereco().toString());
			}
			
			clienteNfeVo.setEndCli(enderecoPadraoPaciente.getLogradouroEndereco());
			
			if(enderecoPadraoPaciente.getNroLogradouro() != null){
			   clienteNfeVo.setNenCli(enderecoPadraoPaciente.getNroLogradouro().toString());
			}
			
			clienteNfeVo.setBaiCli(enderecoPadraoPaciente.getBairroEndereco());
			clienteNfeVo.setCidCli(enderecoPadraoPaciente.getCidadeEndereco());
			clienteNfeVo.setSigUfs(enderecoPadraoPaciente.getUfEndereco());
			clienteNfeVo.setCplEnd(enderecoPadraoPaciente.getComplLogradouro());			
		}
		else {			
			//this.setAghResponsavel(this.cadastrosApoioExamesFacade.obterResponsavelPorSeq(this.getSeq()));
			
			clienteNfeVo.setApeCli(responsavel.getNome());
			clienteNfeVo.setNomCli(responsavel.getNome());
			AghPaisBcb paisResponsavel = this.examesFacade.obterAghPaisBcb(responsavel.getAghPaisBcb().getSeq());
			
			
			if (paisResponsavel.equals(paisBrasil)){
				clienteNfeVo.setTipMer("I");
				clienteNfeVo.setDocumento(responsavel.getCpfCgc().toString());
				clienteNfeVo.setTipCli(responsavel.getDominioCpfCgc().toString());
				clienteNfeVo.setSigUfs(responsavel.getAipUf().getSigla());
			}
			else {
				clienteNfeVo.setTipMer("E");
				clienteNfeVo.setDocumento(responsavel.getNroDocExterior());
				clienteNfeVo.setTipCli("F");
				clienteNfeVo.setSigUfs("EX");
			}
			
			Integer codigoBcB = paisResponsavel.getCodigoBcb();
			
			if(codigoBcB != null){
			   clienteNfeVo.setCodPai(String.format("%04d", codigoBcB));
			}
			clienteNfeVo.setIntNet(responsavel.getEmail());
			clienteNfeVo.setEmaNfe(responsavel.getEmail());
			
			if(responsavel.getCep() != null){
			   clienteNfeVo.setCepCli(responsavel.getCep().toString());
			}
			
			clienteNfeVo.setEndCli(responsavel.getLogradouro());
			
			if(responsavel.getNroLogradouro() != null){
			   clienteNfeVo.setNenCli(responsavel.getNroLogradouro().toString());
			}
			
			clienteNfeVo.setBaiCli(responsavel.getBairro());
			clienteNfeVo.setCidCli(responsavel.getCidade());
			clienteNfeVo.setCplEnd(responsavel.getComplLogradouro());			
			
			if (responsavel.getAipBairrosCepLogradouro() != null) {
				
					cepCadastrado = cadastroPacienteFacade.obterVAipCeps(
							responsavel.getAipBairrosCepLogradouro().getId().getCloCep(),
							responsavel.getAipBairrosCepLogradouro().getId().getCloLgrCodigo(),
							responsavel.getAipBairrosCepLogradouro().getId().getBaiCodigo());
					
			}
			else {			    
				cepCadastrado = cadastroPacienteFacade.obterVAipCeps(
							responsavel.getCep(), null, null);
				
			}
			
			if (cepCadastrado != null){
				this.setarCep(cepCadastrado, clienteNfeVo);
				this.setarLogradouro(cepCadastrado, clienteNfeVo);
								
				cidadeEnderecoCadastrado = cepCadastrado.getAipCidade();
				
				if (cidadeEnderecoCadastrado != null){
				    clienteNfeVo.setCidCli(cidadeEnderecoCadastrado.getNome());
				}
				if (cepCadastrado.getUfSigla() != null && cidadeEnderecoCadastrado.getAipUf() == null){
					cidadeEnderecoCadastrado.setAipUf(cadastrosBasicosPacienteFacade.obterUF(cepCadastrado.getUfSigla()));
				}
				if (cepCadastrado.getAipBairrosCepLogradouro() != null &&
					cepCadastrado.getAipBairrosCepLogradouro().getAipBairro() != null) {
					clienteNfeVo.setBaiCli(this.cadastroPacienteFacade.recarregarCepLogradouro(cepCadastrado.getAipBairrosCepLogradouro())
							.getAipBairro().getDescricao());
				}				
			}
		}
		
		this.setarBairroPadrao(clienteNfeVo);		
		return seniorService.gravarClienteNota(clienteNfeVo);
	}
	
	public void setarBairroPadrao(ClienteNfeVO clienteNfeVo) throws ApplicationBusinessException{
		if (StringUtils.isBlank(clienteNfeVo.getBaiCli())){
			String bairroPadrao = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_BAIRRO_PADRAO);
			clienteNfeVo.setBaiCli(bairroPadrao);		
		}
	}
	
	public void setarCep(VAipCeps cepCadastrado, ClienteNfeVO clienteNfeVo){
		if (cepCadastrado.getId().getCep() != null){
		    clienteNfeVo.setCepCli(cepCadastrado.getId().getCep().toString());
		}
	}
	public void setarLogradouro(VAipCeps cepCadastrado, ClienteNfeVO clienteNfeVo){
		if (cepCadastrado.getLogradouro() != null){
		    clienteNfeVo.setEndCli(cepCadastrado.getLogradouro());
		}
	}
	
	
	
	
	public void salvarResponsavel(AghResponsavel responsavel, Integer seqRespPacInternacao) throws ApplicationBusinessException {
		AghResponsavel responsavelOld = this.aghResponsavelDAO.obterOriginal(responsavel);
		if(responsavel.getAipPaciente()!=null) {
			AipPacientes paciente = pacienteFacade.obterPaciente(responsavel.getAipPaciente().getCodigo());
			if(paciente.getCpf()!=null && responsavel.getEmail()!=null) {
				AipEnderecosPacientes enderecoPadraoPaciente = this.cadastroPacienteFacade.obterEnderecoResidencialPadraoPaciente(paciente);
				if(enderecoPadraoPaciente!=null) {
					AghResponsavelON newResponsavel = (AghResponsavelON) ctx.getBusinessObject(AghResponsavelON.class);
					Long codClienteNfe = newResponsavel.enviarCliente(responsavel);
					responsavel.setCodigoClienteNfe(codClienteNfe);
				}
			}
		}
		else if(responsavel.getEmail()!=null && responsavel.getCpfCgc()!=null) {
				this.validarCPFCGC(responsavel);
				this.validarDocumentoDuplicado(responsavel);
				AghResponsavelON newResponsavel = (AghResponsavelON) ctx.getBusinessObject(AghResponsavelON.class);
				Long codClienteNfe = newResponsavel.enviarCliente(responsavel);
				responsavel.setCodigoClienteNfe(codClienteNfe);
		}
		
		/*** SETA SERVIDOR E DATA ***/
        RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		responsavel.setServidor(servidorLogado);
		responsavel.setCriadoEm(new Date());
		
		if (responsavel.getSeq() == null){
		    this.aghResponsavelDAO.persistir(responsavel);
		    this.aghResponsavelDAO.flush();
		}
		else {
		    this.aghResponsavelDAO.atualizar(responsavel);
		    atualizarResponsavelJn(responsavel,responsavelOld);
		}
		
		if (seqRespPacInternacao != null){
			AinResponsaveisPaciente ainResponsavelPaciente = this.internacaoFacade.obterResponsaveisPaciente(seqRespPacInternacao);
			if (ainResponsavelPaciente != null && ainResponsavelPaciente.getSeq() != null){
			   ainResponsavelPaciente.setResponsavelConta(responsavel);
			   this.internacaoFacade.atualizaResponsaveisPaciente(ainResponsavelPaciente);
			}
		}		
		
	}
@SuppressWarnings("PMD.CyclomaticComplexity")	
	public void atualizarResponsavelJn(AghResponsavel responsavel, AghResponsavel responsavelOld){
		
	     if (CoreUtil.modificados(responsavel.getCpfCgc(), responsavelOld.getCpfCgc())
			|| CoreUtil.modificados(responsavel.getNroDocExterior(), responsavelOld.getNroDocExterior())
			|| CoreUtil.modificados(responsavel.getNome(), responsavelOld.getNome())
			|| CoreUtil.modificados(responsavel.getNomeMae(), responsavelOld.getNomeMae())
			|| CoreUtil.modificados(responsavel.getDtNascimento(), responsavelOld.getDtNascimento())
			|| CoreUtil.modificados(responsavel.getAghPaisBcb() != null ? responsavel.getAghPaisBcb().getSeq() : null, responsavelOld.getAghPaisBcb() != null ? responsavelOld.getAghPaisBcb().getSeq() : null)
			|| CoreUtil.modificados(responsavel.getAipBairrosCepLogradouro() != null ? responsavel.getAipBairrosCepLogradouro().getId().getCloLgrCodigo() : null, responsavelOld.getAipBairrosCepLogradouro() != null ? responsavelOld.getAipBairrosCepLogradouro().getId().getCloLgrCodigo() : null)
			|| CoreUtil.modificados(responsavel.getAipBairrosCepLogradouro() != null ? responsavel.getAipBairrosCepLogradouro().getId().getCloCep() : null , responsavelOld.getAipBairrosCepLogradouro() != null ? responsavelOld.getAipBairrosCepLogradouro().getId().getCloCep() : null)
			|| CoreUtil.modificados(responsavel.getAipBairrosCepLogradouro() != null ? responsavel.getAipBairrosCepLogradouro().getId().getBaiCodigo() : null , responsavelOld.getAipBairrosCepLogradouro() != null ? responsavelOld.getAipBairrosCepLogradouro().getId().getBaiCodigo() : null)
			|| CoreUtil.modificados(responsavel.getAipCidade() != null ? responsavel.getAipCidade().getCodigo() : null, responsavelOld.getAipCidade() != null ? responsavelOld.getAipCidade().getCodigo() : null)
			|| CoreUtil.modificados(responsavel.getCidade(), responsavelOld.getCidade())
			|| CoreUtil.modificados(responsavel.getLogradouro(), responsavelOld.getLogradouro())
			|| CoreUtil.modificados(responsavel.getComplLogradouro(), responsavelOld.getComplLogradouro())
			|| CoreUtil.modificados(responsavel.getNroLogradouro(), responsavelOld.getNroLogradouro())
			|| CoreUtil.modificados(responsavel.getCep(), responsavelOld.getCep())
			|| CoreUtil.modificados(responsavel.getBairro(), responsavelOld.getBairro())
			|| CoreUtil.modificados(responsavel.getAipUf() != null ? responsavel.getAipUf().getSigla() : null, responsavelOld.getAipUf() != null ? responsavelOld.getAipUf().getSigla() : null)
			|| CoreUtil.modificados(responsavel.getUfSiglaExterior(), responsavelOld.getUfSiglaExterior())
			|| CoreUtil.modificados(responsavel.getRg(), responsavelOld.getRg())
			|| CoreUtil.modificados(responsavel.getAipOrgaosEmissor() != null ? responsavel.getAipOrgaosEmissor().getCodigo() : null, responsavelOld.getAipOrgaosEmissor() != null ? responsavelOld.getAipOrgaosEmissor().getCodigo() : null)
			|| CoreUtil.modificados(responsavel.getPisPasep(), responsavelOld.getPisPasep())
			|| CoreUtil.modificados(responsavel.getDddFone(), responsavelOld.getDddFone())
			|| CoreUtil.modificados(responsavel.getFone(), responsavelOld.getFone())
			|| CoreUtil.modificados(responsavel.getEmail(), responsavelOld.getEmail())
			|| CoreUtil.modificados(responsavel.getCriadoEm(), responsavelOld.getCriadoEm())
			|| CoreUtil.modificados(responsavel.getAipPaciente() != null ? responsavel.getAipPaciente().getCodigo() : null, responsavelOld.getAipPaciente() != null ? responsavelOld.getAipPaciente().getCodigo() : null)
			|| CoreUtil.modificados(responsavel.getCodigoClienteNfe(), responsavelOld.getCodigoClienteNfe())
			|| CoreUtil.modificados(responsavel.getRegNascimento(), responsavelOld.getRegNascimento())) {
		
		criarAghResponsavelJn(responsavelOld ,DominioOperacoesJournal.UPD);
		
	     }	
	}
	
@SuppressWarnings("PMD.NPathComplexity")
	public void criarAghResponsavelJn(AghResponsavel responsavelConta,	DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		final AghResponsavelJn responsavelContaJn = BaseJournalFactory.getBaseJournal(operacao, AghResponsavelJn.class
				, servidorLogado.getUsuario());
		
		responsavelContaJn.setDominioCpfCgc(responsavelConta.getDominioCpfCgc());
		responsavelContaJn.setCpfCgc(responsavelConta.getCpfCgc());
		responsavelContaJn.setNroDocExterior(responsavelConta.getNroDocExterior());
		responsavelContaJn.setNome(responsavelConta.getNome());
		responsavelContaJn.setNomeMae(responsavelConta.getNomeMae());
		responsavelContaJn.setDtNascimento(responsavelConta.getDtNascimento());
		responsavelContaJn.setAghPaisBcb(responsavelConta.getAghPaisBcb() != null ? responsavelConta.getAghPaisBcb().getSeq() : null);
		idBairroCepLogradouro(responsavelConta, responsavelContaJn);
		responsavelContaJn.setAipCidade(responsavelConta.getAipCidade() != null ? responsavelConta.getAipCidade().getCodigo() : null);
		responsavelContaJn.setCidade(responsavelConta.getCidade());
		responsavelContaJn.setLogradouro(responsavelConta.getLogradouro());
		responsavelContaJn.setComplLogradouro(responsavelConta.getComplLogradouro());
		responsavelContaJn.setNroLogradouro(responsavelConta.getNroLogradouro());
		responsavelContaJn.setCep(responsavelConta.getCep());
		responsavelContaJn.setBairro(responsavelConta.getBairro());
		responsavelContaJn.setAipUf(responsavelConta.getAipUf() != null ? responsavelConta.getAipUf().getSigla() : null);
		responsavelContaJn.setUfSiglaExterior(responsavelConta.getUfSiglaExterior());
		responsavelContaJn.setRg(responsavelConta.getRg());
		responsavelContaJn.setAipOrgaosEmissor(responsavelConta.getAipOrgaosEmissor() != null ? responsavelConta.getAipOrgaosEmissor().getCodigo() : null);
		responsavelContaJn.setPisPasep(responsavelConta.getPisPasep());
		responsavelContaJn.setDddFone(responsavelConta.getDddFone());
		responsavelContaJn.setFone(responsavelConta.getFone());
		responsavelContaJn.setEmail(responsavelConta.getEmail());
		responsavelContaJn.setCriadoEm(responsavelConta.getCriadoEm());
		responsavelContaJn.setMatricula(servidorLogado.getServidor().getId().getMatricula());
		responsavelContaJn.setVinCodigo(servidorLogado.getServidor().getId().getVinCodigo());
		responsavelContaJn.setAipPaciente(responsavelConta.getAipPaciente() != null ? responsavelConta.getAipPaciente().getCodigo() : null);
		responsavelContaJn.setCodigoClienteNfe(responsavelConta.getCodigoClienteNfe() != null ? responsavelConta.getCodigoClienteNfe() : null);
		responsavelContaJn.setRegNascimento(responsavelConta.getRegNascimento());
		
		this.getAghResponsavelJnDAO().persistir(responsavelContaJn);
		this.flush();
	}
	
	private void idBairroCepLogradouro(AghResponsavel responsavelConta,
			final AghResponsavelJn responsavelContaJn) {
		AipBairrosCepLogradouroId id = responsavelConta.getAipBairrosCepLogradouro() != null ? responsavelConta.getAipBairrosCepLogradouro().getId() : null;
		responsavelContaJn.setCloLgrCodigo(id != null ? id.getCloLgrCodigo() : null);
		responsavelContaJn.setCloCep(id != null ? id.getCloCep() : null);
		responsavelContaJn.setBaiCodigo(id != null ? id.getBaiCodigo(): null);
	}
	
	
	public void validarCPFCGC(AghResponsavel responsavel) throws ApplicationBusinessException {
		if (DominioCpfCgcResponsavel.F.equals(responsavel.getDominioCpfCgc())){
			if (!CoreUtil.validarCPF(responsavel.getCpfCgc().toString())) {
				throw new ApplicationBusinessException(ResponsavelRNExceptionCode.CPF_INVALIDO_RESPONSAVEL);
			}
		}
		else if (DominioCpfCgcResponsavel.J.equals(responsavel.getDominioCpfCgc())){
			if (!CoreUtil.validarCNPJ(responsavel.getCpfCgc().toString())) {
				throw new ApplicationBusinessException(ResponsavelRNExceptionCode.CNPJ_INVALIDO_RESPONSAVEL);
			}
		}
	}
	
	public void validarDocumentoDuplicado(AghResponsavel responsavel) throws ApplicationBusinessException {
		
		if(responsavel.getAipPaciente() == null &&
		   this.aghResponsavelDAO.verificaDocumentoDuplicado(responsavel.getSeq(), responsavel.getCpfCgc(), responsavel.getNroDocExterior())){
			throw new ApplicationBusinessException(ResponsavelRNExceptionCode.DOCUMENTO_DUPLICADO_RESPONSAVEL);
		}
		
	}	
	
	public List<ResponsavelVO> listarResponsavel(String parametro) {
		
		List<AghResponsavel> listaResponsaveis = this.aghResponsavelDAO.listarResponsavel(parametro);
		
		List<ResponsavelVO> listResponsavelVo = new ArrayList<ResponsavelVO>();
		for(AghResponsavel resp : listaResponsaveis ){
			ResponsavelVO respVo = this.obterResponsavelVo(resp);
			respVo.setAghResponsavel(resp);
			listResponsavelVo.add(respVo);
		} 
		
		return listResponsavelVo;
	}
	
	public ResponsavelVO obterResponsavelVo(AghResponsavel resp){
		if (resp == null || resp.getSeq() == null ){
			return null;
		}
		
		resp = this.aghResponsavelDAO.obterResponsavelPorSeq(resp.getSeq());
		ResponsavelVO respVo = new ResponsavelVO();
		respVo.setSeq(resp.getSeq());
		if(resp.getAipPaciente() != null){
		   respVo.setNome(resp.getAipPaciente().getNome());
		   if(resp.getAipPaciente().getCpf()!=null) {
			   respVo.setDocumento(resp.getAipPaciente().getCpf().toString());
		   }
		}
		else {
			respVo.setNome(resp.getNome());
			
			if (resp.getNroDocExterior() != null){
				respVo.setDocumento(resp.getNroDocExterior() );
			}
			else {
				if (resp.getCpfCgc() != null){
				    respVo.setDocumento(resp.getCpfCgc().toString());
				}
			}
			
		}
		respVo.setAghResponsavel(resp);
		return respVo;
	}
	
	//getters	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public AghResponsavelJnDAO getAghResponsavelJnDAO() {
		return aghResponsavelJnDAO;
	}
	public AghResponsavelDAO getAghResponsavelDAO() {
		return aghResponsavelDAO;
	}
	
}