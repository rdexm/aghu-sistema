package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.certificacaodigital.service.ICertificacaoDigitalService;
import br.gov.mec.aghu.dominio.DominioEventoNotaAdicional;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoNotaAdicional;
import br.gov.mec.aghu.model.McoNotaAdicionalId;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.perinatologia.dao.McoNotaAdicionalDAO;
import br.gov.mec.aghu.perinatologia.dao.McoRecemNascidosDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;


@Stateless
public class NotaAdicionalRN extends BaseBusiness {

	private static final long serialVersionUID = -2168451805689500113L;
	private static final Log LOG = LogFactory.getLog(NotaAdicionalRN.class);
	private static final String ASSINATURA_DIGITAL = "ASSINATURA DIGITAL";

	public enum NotaAdicionalRNExceptionCode implements BusinessExceptionCode {	
		ERRO_AO_GRAVAR_NOTA_ADICIONAL,
		MESSAGEM_ERRO_SERVICO,
		MENSAGEM_SERVICO_INDISPONIVEL,
		MENSAGEM_ERRO_OBTER_PARAMETRO;
		
	}	
	
	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	@EJB
	private ICertificacaoDigitalService certificacaoDigitalService;
	
	@EJB
	private IPacienteService pacienteService;
	
	@Inject
	private McoNotaAdicionalDAO notaAdicionalDAO; 
	
	@Inject
	private McoRecemNascidosDAO recemNascidosDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}	

	public void gravarNotaAdicional(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero,
			DominioEventoNotaAdicional evento, String notaAdicional) throws ApplicationBusinessException {
		try {	
						
			McoNotaAdicionalId id = new McoNotaAdicionalId();
			id.setGsoPacCodigo(gsoPacCodigo);
			id.setGsoSeqp(gsoSeqp);
			id.setSeqp(this.notaAdicionalDAO.obterMaxSeqpMcoNotaAdicional(gsoPacCodigo, gsoSeqp));

			McoNotaAdicional mcoNotaAdicional = new McoNotaAdicional();
			mcoNotaAdicional.setId(id);
			mcoNotaAdicional.setConNumero(conNumero);
			
			McoRecemNascidos recemNascido = this.recemNascidosDAO.obterMcoRecemNascidosPorId(gsoPacCodigo, gsoSeqp, id.getSeqp());
			mcoNotaAdicional.setRnaSeqp(recemNascido != null && recemNascido.getId() != null ? recemNascido.getId().getSeqp().shortValue() : null);
			
			mcoNotaAdicional.setEvento(evento);
			mcoNotaAdicional.setNotaAdicional(notaAdicional);
			mcoNotaAdicional.setCriadoEm(new Date());
			mcoNotaAdicional.setSerMatricula(this.usuario.getMatricula());
			mcoNotaAdicional.setSerVinCodigo(this.usuario.getVinculo());

			this.notaAdicionalDAO.persistir(mcoNotaAdicional);
			
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(NotaAdicionalRNExceptionCode.ERRO_AO_GRAVAR_NOTA_ADICIONAL);
		}
	}
	
	public boolean gerarPendenciaDeAssinaturaDigitalDoUsuarioLogado() throws ApplicationBusinessException {
		try{ 
			boolean gerarPendenciaDeAssinaturaDigital = false;
			Integer matricula = this.usuario.getMatricula();
			Short vinCodigo = this.usuario.getVinculo();
			
			String habilitaCertificacaoDigital = obtemParametroPorNome(EmergenciaParametrosEnum.P_HABILITA_CERTIF.toString());
			//Executa servico #37571 
			Boolean certificado = certificacaoDigitalService.verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado(matricula, vinCodigo);
			
			if(habilitaCertificacaoDigital.equalsIgnoreCase("S") && certificado){
				//Executa servico #41092
				if(pacienteService.verificarAcaoQualificacaoMatricula(ASSINATURA_DIGITAL)){
					gerarPendenciaDeAssinaturaDigital = true;
				}				
			}
			return gerarPendenciaDeAssinaturaDigital;
			
		} catch (ServiceBusinessException e) {
			throw new ApplicationBusinessException(NotaAdicionalRNExceptionCode.MESSAGEM_ERRO_SERVICO, e.getMessage());
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(NotaAdicionalRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(NotaAdicionalRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}
	
	private String obtemParametroPorNome(String nome) throws ApplicationBusinessException, ServiceException {
		Object parametro = parametroFacade.obterAghParametroPorNome(nome, "vlrTexto");
		if(parametro == null) {
			throw new ApplicationBusinessException(NotaAdicionalRNExceptionCode.MENSAGEM_ERRO_OBTER_PARAMETRO, nome);			
		}
		return (String) parametro;
	}
	
}
