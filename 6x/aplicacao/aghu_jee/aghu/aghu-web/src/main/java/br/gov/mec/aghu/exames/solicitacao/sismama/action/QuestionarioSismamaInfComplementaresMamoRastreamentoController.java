package br.gov.mec.aghu.exames.solicitacao.sismama.action;



import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;


public class QuestionarioSismamaInfComplementaresMamoRastreamentoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(QuestionarioSismamaInfComplementaresMamoRastreamentoController.class);
	
	public static Log getLog() {
		return LOG;
	}

	private static final long serialVersionUID = -228994132337845488L;
	
		protected Boolean cTmpMamRastAlvo = false;
		protected Boolean cTmpMamRastRisco = false;
		protected Boolean cTmpMamRastTrat = false;
		
		public void limpar(){
			this.cTmpMamRastAlvo = false;
			this.cTmpMamRastRisco = false;
			this.cTmpMamRastTrat = false;
		}
		
		public void habilitarCamposMamoRastreamento(
				Boolean habilitar) {
			
			this.cTmpMamRastAlvo = habilitar;
			this.cTmpMamRastRisco = habilitar;
			this.cTmpMamRastTrat = habilitar;
		}
		
		public Boolean getcTmpMamRastAlvo() {
			return cTmpMamRastAlvo;
		}

		public void setcTmpMamRastAlvo(Boolean cTmpMamRastAlvo) {
			this.cTmpMamRastAlvo = cTmpMamRastAlvo;
		}

		public Boolean getcTmpMamRastRisco() {
			return cTmpMamRastRisco;
		}

		public void setcTmpMamRastRisco(Boolean cTmpMamRastRisco) {
			this.cTmpMamRastRisco = cTmpMamRastRisco;
		}

		public Boolean getcTmpMamRastTrat() {
			return cTmpMamRastTrat;
		}

		public void setcTmpMamRastTrat(Boolean cTmpMamRastTrat) {
			this.cTmpMamRastTrat = cTmpMamRastTrat;
		}

}
