package br.gov.mec.aghu.paciente.business;

import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * 
 * Classe de apoio para as Business Facades. Ela em geral agrupa as
 * funcionalidades encontradas em packages e procedures do AGHU.
 * 
 * Ela poderia ser uma classe com acesso friendly ou default e não ser um
 * componente seam.
 * 
 * Mas fazendo assim facilita, pois ela também pode receber uma referência para
 * o EntityManager.
 * 
 * Outra forma de fazer é instanciar ela diretamente do ON e passar o entity
 * manager para seus parâmetros. Neste caso os metodos desta classe poderiam ser
 * até estaticos e nao necessitar de instanciação. Ai ela seria apenas um
 * particionamento lógico de código e não um componente que possa ser injetado
 * em qualquer outro contexto.
 * 
 * ATENÇÃO, Os metodos desta classe nunca devem ser acessados diretamente por
 * qualquer classe que não a ON correspondente. Por isso sugere-se que todos os
 * métodos desta sejam friendly (default) ou private.
 * 
 */
@Stateless
public class McokEfiRN extends BaseBusiness {


@EJB
private AtendimentosRN atendimentosRN;

@EJB
private AtendimentoJournalRN atendimentoJournalRN;

private static final Log LOG = LogFactory.getLog(McokEfiRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAghuFacade aghuFacade;

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2657862237240564433L;

	private enum McokEfiRNExceptionCode implements BusinessExceptionCode {
		MCO_00546, MCO_00532
	}	

	public void rnEfipVerDilat(String pDilatacao) throws ApplicationBusinessException {
		if(pDilatacao != null) {
			if(Long.parseLong(pDilatacao) < 0L || Long.parseLong(pDilatacao) >= 11L) {
				throw new ApplicationBusinessException(McokEfiRNExceptionCode.MCO_00546);
			}
		}
	}

	public void rnEfipVerDtCons(String pDilatacao) throws ApplicationBusinessException {
		/* dthr_consulta se informada, deve ser menor que sysdate e maior que a (sysdate-3) */
		//-- qdo vem da procedure aipp_subs_pac_gesta não deve testar esta regra
		if(Boolean.FALSE.equals(this.getvVeioTrocaPac())) {
	    	Date dataAtual = new Date();	    	
	    	Date dataAtual3DiasAntes = DateUtils.addDays(dataAtual, -3);
	    	Date dataPDilatacao = null;
	    	try {
	    		dataPDilatacao = DateUtils.parseDate(pDilatacao, new String[]{"dd/MM/yyyy"}); 
	    	} catch(ParseException e) {
	    		throw new ApplicationBusinessException(McokEfiRNExceptionCode.MCO_00546);
	    	}
			
	    	if(dataPDilatacao.after(dataAtual) || dataPDilatacao.before(dataAtual3DiasAntes)) {
				throw new ApplicationBusinessException(McokEfiRNExceptionCode.MCO_00532);
			}
		}
	}

	public void rnEfipAtuAtend(Integer pConNumero, Integer pGsoPacCodigo, Short pGsoSeqp, String nomeMicrocomputador) throws BaseException {
		IAghuFacade aghuFacade = this.getAghuFacade();
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		/*Atualizar o Atendimento vigente para a paciente com a PK da gestação */
		AghAtendimentos aghAtendimento = aghuFacade.obterAtendimentoPorNumeroConsulta(pConNumero);
		
		AghAtendimentos aghAtendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(aghAtendimento);
		
		//TODO: Tarefa #14529 - Troca de uso do atributo mcoGestacoes pelos gsoPacCodigo e gsoSeqp. Avaliar 
		//quando for implantado a perinatologia se o código comentado deverá retornar
		//aghAtendimento.setMcoGestacoes(this.entityManager.find(McoGestacoes.class, new McoGestacoesId(pGsoPacCodigo, pGsoSeqp)));
		aghAtendimento.setGsoPacCodigo(pGsoPacCodigo);
		aghAtendimento.setGsoSeqp(pGsoSeqp);
		
		this.getAtendimentosRN().atualizarAtendimento(aghAtendimento, aghAtendimentoOld, nomeMicrocomputador, servidorLogado, new Date());
	}

	public Boolean getvVeioTrocaPac() {
		return Boolean.TRUE.equals((Boolean) this.obterContextoSessao("MCOK_EFI_RN_V_VEIO_TROCA_PAC"));
	}

	public void setvVeioTrocaPac(Boolean vVeioTrocaPac) {
	//	this.atribuirContextoSessao(VariaveisSessaoEnum.MCOK_EFI_RN_V_VEIO_TROCA_PAC, vVeioTrocaPac);
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected AtendimentosRN getAtendimentosRN() {
		return atendimentosRN;
	}
	
	protected AtendimentoJournalRN getAtendimentoJournalRN() {
		return atendimentoJournalRN;
	}
	
}
